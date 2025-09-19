package com.training.ec.controller;

import java.math.BigDecimal;
import java.util.List;

// import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.training.ec.entity.AccountUserDetails;
import com.training.ec.entity.Stock;
import com.training.ec.entity.UserStock;
import com.training.ec.service.TradeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user/stock")
@RequiredArgsConstructor
public class PortfolioController {

    private final TradeService tradeService;
    // private final SimpMessagingTemplate messagingTemplate; // WebSocket 送信用

    /**
     * 保有株一覧を表示
     */
    @GetMapping("/portfolio")
    @PreAuthorize("hasRole('USER')")
    public String showPortfolio(@AuthenticationPrincipal AccountUserDetails userDetails,
            Model model) {
        Integer userId = userDetails.getUserId();
        List<UserStock> userStocks = tradeService.getUserStocks(userId);

        model.addAttribute("userStocks", userStocks);
        return "user/stock/portfolio";
    }

    /**
     * 売却入力ページ
     */
    @GetMapping("/sell/{stockId}")
    @PreAuthorize("hasRole('USER')")
    public String showSellForm(@PathVariable Long stockId,
            @AuthenticationPrincipal AccountUserDetails userDetails,
            Model model) {
        Integer userId = userDetails.getUserId();
        UserStock userStock = tradeService.findUserStock(userId, stockId);

        if (userStock == null) {
            throw new IllegalStateException("保有していない株です: stockId=" + stockId);
        }
         // ✅ チャート用の銘柄情報も渡す
        Stock stock = tradeService.findStockById(stockId);

        model.addAttribute("userStock", userStock);
        model.addAttribute("stock",stock);
        return "user/stock/sell/sell"; // sell.html（数量を入力する画面）
    }

    /**
     * 売却確認画面
     */
    @PostMapping("/sell/confirm")
    @PreAuthorize("hasRole('USER')")
    public String confirmSell(@RequestParam Long stockId,
            @RequestParam int quantity,
            @AuthenticationPrincipal AccountUserDetails userDetails,
            Model model) {
        Integer userId = userDetails.getUserId();
        UserStock userStock = tradeService.findUserStock(userId, stockId);

        if (userStock == null) {
            throw new IllegalStateException("保有していない株です: stockId=" + stockId);
        }

        // ✅ グラフ用に銘柄情報を追加で渡す
        Stock stock = tradeService.findStockById(stockId);
        model.addAttribute("stock", stock); // ← 追加

        // 合計売却金額を計算
        BigDecimal totalPrice = userStock.getPrice().multiply(BigDecimal.valueOf(quantity));
        model.addAttribute("userStock", userStock);
        model.addAttribute("quantity", quantity);      // ← HTML で使う
        model.addAttribute("totalPrice", totalPrice);
        return "user/stock/sell/sellconfirm";
    }

    /**
     * 売却実行 → 完了画面へ
     */
    @PostMapping("/sell/execute")
    @PreAuthorize("hasRole('USER')")
    public String executeSell(@AuthenticationPrincipal AccountUserDetails userDetails,
            @RequestParam Long stockId,
            @RequestParam int quantity,
            Model model) {
        Integer userId = userDetails.getUserId();

        tradeService.sellStock(userId, stockId, quantity);

        model.addAttribute("stockId", stockId);
        model.addAttribute("quantity", quantity);
        return "user/stock/sell/sellcomplete";
    }
}
