package com.training.ec.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes; // セッション管理用
import org.springframework.web.bind.support.SessionStatus; // セッションクリア用
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.training.ec.entity.Stock;
import com.training.ec.form.StockForm;
import com.training.ec.service.StockService;
import com.training.ec.service.UserService;

/**
 * 管理者向けの機能を制御するコントローラーです。
 * 銘柄の登録、一覧表示、ユーザー権限の管理などを行います。
 */
@Controller
@RequestMapping("/admin") // このコントローラーのベースパス
@SessionAttributes("stockForm") // stockFormオブジェクトをセッションに保持
public class adminController {

    private static final Logger logger = LoggerFactory.getLogger(adminController.class);

    private final UserService userService;
    private final StockService stockService;

    @Autowired
    public adminController(UserService userService, StockService stockService) {
        this.userService = userService;
        this.stockService = stockService;
    }

    // ---------------------- ログイン・メニュー関連 ----------------------
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }

    /**
     * 管理者ホーム画面を表示するメソッド ADMINロールを持つユーザーのみアクセス可能
     */
    @GetMapping("/menu")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminHome(Model model) {
        logger.info("Admin user accessed the admin home page.");
        return "admin/menu";
    }

    /**
     * ユーザーのロールを更新するAPIエンドポイント ADMINロールを持つユーザーのみアクセス可能
     */
    @PostMapping("/user/update-role")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUserRole(@RequestParam("userId") Integer userId, @RequestParam("newRoleName") String newRoleName) {
        logger.info("Attempting to update role for user ID: {} to role: {}", userId, newRoleName);
        try {
            userService.updateUserRole(userId, newRoleName);
            logger.info("Successfully updated role for user ID: {}", userId);
            return "Role updated successfully!";
        } catch (Exception e) {
            logger.error("Failed to update role for user ID: {}", userId, e);
            return "Failed to update role: " + e.getMessage();
        }
    }

    // ---------------------- 銘柄関連 ----------------------

    /**
     * 銘柄一覧ページを表示します。
     * 検索機能に対応し、キーワードに基づいて銘柄をフィルタリングします。
     *
     * @param searchTerm 検索キーワード (オプション)
     * @param model モデルオブジェクト
     * @return 銘柄一覧画面のビュー名
     */
    @GetMapping("/stocks")
    @PreAuthorize("hasRole('ADMIN')")
    public String showStockList(@RequestParam(name = "searchTerm", required = false) String searchTerm, Model model) {
        List<Stock> stocks = stockService.getStocksBySearchTerm(searchTerm);
        model.addAttribute("stocks", stocks);
        model.addAttribute("searchTerm", searchTerm);
        return "admin/stocks/stockList";
    }

    /**
     * 新規銘柄登録フォームを表示します。
     *
     * @param model モデル
     * @return 登録フォームのビュー名
     */
    @GetMapping("/stocks/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String showRegisterForm(Model model) {
        model.addAttribute("stockForm", new StockForm());
        return "admin/stocks/register";
    }

    /**
     * フォームから送信されたデータを受け取り、確認画面へ遷移します。
     *
     * @param stockForm フォームデータ
     * @param bindingResult バリデーション結果
     * @return 確認画面のビュー名
     */
    @PostMapping("/stocks/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmRegistration(@Validated @ModelAttribute("stockForm") StockForm stockForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/stocks/register";
        }
        return "admin/stocks/confirm";
    }

    /**
     * 確認画面から送信されたデータを受け取り、銘柄をデータベースに登録します。
     *
     * @param stockForm セッションから取得したフォームデータ
     * @param redirectAttributes リダイレクト時にメッセージを保持
     * @param sessionStatus セッションの完了状態を管理
     * @return リダイレクト先URL
     */
    @PostMapping("/stocks/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public String completeRegistration(@Validated StockForm stockForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, SessionStatus sessionStatus) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "登録内容に不備があります。再度入力してください。");
            return "redirect:/admin/stocks/register";
        }

        try {
            stockService.createStock(stockForm);
            redirectAttributes.addFlashAttribute("successMessage", "新しい銘柄が正常に登録されました。");
            sessionStatus.setComplete(); // セッションをクリア
            return "redirect:/admin/stocks/complete";
        } catch (Exception e) {
            logger.error("銘柄の登録中にエラーが発生しました。", e);
            redirectAttributes.addFlashAttribute("errorMessage", "銘柄の登録に失敗しました。");
            return "redirect:/admin/stocks/register";
        }
    }
    
    /**
     * 登録完了画面を表示します。
     */
    @GetMapping("/stocks/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCompletePage() {
        return "admin/stocks/complete";
    }
}
