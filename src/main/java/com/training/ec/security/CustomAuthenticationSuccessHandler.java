package com.training.ec.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 認証成功時の処理。
     * ユーザーの権限（ROLE）に応じて、リダイレクト先を振り分ける。
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 認証されたユーザーのロールをチェック
        // Stream APIを使用して、権限コレクション内に特定の権限があるかを確認
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // ロールが"ADMIN"の場合、/admin/menuにリダイレクト
            response.sendRedirect("/admin/menu");
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            // ロールが"USER"の場合、/user/userにリダイレクト
            response.sendRedirect("/user/user");
        } else {
            // どのロールにも該当しない場合、公開されているホーム画面にリダイレクト
            // これにより、予期せぬエラーや無限リダイレクトを防ぐ
            response.sendRedirect("/home");
        }
    }
}
