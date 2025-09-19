package com.training.ec.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.training.ec.form.UserRegistrerform;
import com.training.ec.service.UserService;

/**
 * ユーザー登録画面の表示と登録処理を担当するコントローラークラス
 */
@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("userRegistrerform", new UserRegistrerform());
        return "register/register";
    }

    @PostMapping("/register")

    public String register(@Validated @ModelAttribute UserRegistrerform form,
            BindingResult result,
            Model model) {
        // バリデーションエラーのチェックは残します
        // これにより、フォームクラスで定義された全てのバリデーションが自動的に実行されます
        if (result.hasErrors()) {
            // エラーがある場合は、登録フォーム画面に戻る
            return "register/register";
        }

        try {
            // バリデーションが成功した場合、UserServiceの登録処理を呼び出す
            userService.register(form);
            // 登録成功後、ログイン画面へリダイレクトする
            return "redirect:/admin/login";
        } catch (IllegalArgumentException e) {
            // ユーザー名重複など、登録処理で発生するビジネスロジックエラーの処理は残します
            model.addAttribute("errorMessage", e.getMessage());
            return "register/register";
        }
    }
}
