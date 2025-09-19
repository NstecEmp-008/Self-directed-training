package com.training.ec.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.training.ec.entity.AccountUserDetails;
import com.training.ec.service.PortfolioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor 
public class UserController {

    private final PortfolioService portfolioService;

    @GetMapping("/user")
    public String user() {
        return "user/user"; // templates/user/user.html
    }

    /**
     * 総資産画面の表示
     */
    @GetMapping("/portfolio")
    @PreAuthorize("hasRole('USER')")
    public String showTotalAssets(@AuthenticationPrincipal AccountUserDetails userDetails,
                                  Model model) {

        Integer userId = userDetails.getUserId();

        // ✅ サービス層で現金＋株式評価額を合計した値を取得
        var totalAssets = portfolioService.getTotalAssets(userId);

        // モデルに渡す
        model.addAttribute("totalAssets", totalAssets);
        model.addAttribute("userId", userId);


        // 画面遷移がうまくいかないHTML側に問題あるかも
        return "user/myPortfolio"; // → templates/user/myPortfolio.html
    }

}