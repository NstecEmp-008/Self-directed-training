package com.training.ec.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;  // ✅ 追加：WebSocket送信用
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.training.ec.entity.AccountUserDetails;
import com.training.ec.entity.Stock;
import com.training.ec.form.BuyForm;
import com.training.ec.repository.TradeRepository;
import com.training.ec.service.TradeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/user/stock")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final TradeRepository tradeRepository;

    // ✅ 追加：WebSocket配信用の依存を注入
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 購入フォーム表示
     */
    @GetMapping("/buy/{stockId}")
    @PreAuthorize("hasRole('USER')")
    public String showBuyForm(@PathVariable Long stockId,
            @AuthenticationPrincipal AccountUserDetails userDetails,
            Model model) {

        Stock stock = tradeRepository.findStockById(stockId);
        if (stock == null) {
            model.addAttribute("message", "指定された銘柄が存在しません。");
            return "error/404";
        }

        BuyForm buyForm = new BuyForm();
        buyForm.setStockId(stockId); // ✅ 追加：hiddenで送るためにstockIdをセット

        model.addAttribute("stock", stock);
        model.addAttribute("buyForm", buyForm);
        model.addAttribute("userId", userDetails.getUserId());

        // ✅ 追加：最初に画面を開いた時点でも最新株価を配信
        messagingTemplate.convertAndSend("/topic/stock/" + stockId, stock);

        return "user/stock/buy/buy";
    }

    /**
     * 購入確認画面
     */
    @PostMapping("/buy/confirm")
    @PreAuthorize("hasRole('USER')")
    public String confirmBuy(@ModelAttribute("buyForm") BuyForm buyForm,
            @AuthenticationPrincipal AccountUserDetails userDetails,
            Model model,
            HttpSession session) {

        Stock stock = tradeRepository.findStockById(buyForm.getStockId());
        if (stock == null) {
            model.addAttribute("message", "銘柄が存在しません。");
            return "error/404";
        }

        // ✅ 追加：二重送信防止のためトークンをセッションに保存
        String token = UUID.randomUUID().toString();
        session.setAttribute("BUY_TOKEN", token);

        model.addAttribute("stock", stock);
        model.addAttribute("buyForm", buyForm);
        model.addAttribute("totalPrice",
                stock.getPrice().multiply(BigDecimal.valueOf(buyForm.getQuantity())));
        model.addAttribute("buyToken", token);
        model.addAttribute("userId", userDetails.getUserId());

        // ✅ 追加：確認画面でも最新株価を配信
        messagingTemplate.convertAndSend("/topic/stock/" + stock.getId(), stock);

        return "user/stock/buy/confirm";
    }

    /**
     * 購入実行 → 完了画面
     */
    @PostMapping("/buy/execute")
    @PreAuthorize("hasRole('USER')")
    public String executeBuy(@ModelAttribute BuyForm buyForm,
            @AuthenticationPrincipal AccountUserDetails userDetails,
            Model model,
            HttpSession session) {
        try {
            // ✅ 追加：二重送信防止チェック
            String token = (String) session.getAttribute("BUY_TOKEN");
            session.removeAttribute("BUY_TOKEN"); // ✅ 追加：ワンタイム利用 → 即削除

            if (token == null) {
                model.addAttribute("message", "不正なリクエストです。");
                return "error/400";
            }

            // 購入処理実行
            tradeService.buyStock(userDetails.getUserId(),
                    buyForm.getStockId(),
                    buyForm.getQuantity());

            model.addAttribute("message", "購入が完了しました！");

            // ✅ 追加：購入完了後、最新データを配信して他画面も更新されるようにする
            Stock updatedStock = tradeRepository.findStockById(buyForm.getStockId());
            if (updatedStock != null) {
                messagingTemplate.convertAndSend("/topic/stock/" + buyForm.getStockId(), updatedStock);
            } else {
                log.warn("Stock not found: {}", buyForm.getStockId());
            }

        } catch (IllegalStateException e) {
            // ✅ 追加：業務エラー用のcatch
            model.addAttribute("message", "購入できません: " + e.getMessage());
        } catch (RuntimeException e) {
            // ✅ 追加：予期しないエラー用のcatch
            log.error("購入処理中にエラー発生", e);
            model.addAttribute("message", "システムエラーが発生しました。");
        }

        return "user/stock/buy/complete";
    }
}
