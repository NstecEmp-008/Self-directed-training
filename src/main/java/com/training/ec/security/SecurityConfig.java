package com.training.ec.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.firewall.DefaultHttpFirewall;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AccountUserDetailsService service;
    @Autowired
    private PasswordEncoder encoder;

    // 🔽【変更点①】ログイン成功時の処理を担当するハンドラーをDI（依存性注入）
    // CustomAuthenticationSuccessHandler は @Component が付いているため、自動的にSpringが管理してくれる
    @Autowired
    private AuthenticationSuccessHandler successHandler;

    /**
     * Spring Securityの認証時に使うサービスとパスワードエンコーダーを登録
     * 今回はDIしている`AccountUserDetailsService`クラスと`PasswordEncoder`クラスを登録
     *
     * @param userDetailsService
     * @param passwordEncoder
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(service);
        authenticationProvider.setPasswordEncoder(encoder);
        return new ProviderManager(authenticationProvider);
    }

    /**
     * Spring Securityカスタマイズ
     * 今回はpublic配下のURLは認証対象外とすることと、デフォルトのHttpFirewallを使用することを設定
     */
    @Bean
    public WebSecurityCustomizer webCustomizer() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        return (web) -> web
                .httpFirewall(firewall)
                // 修正: /public/** の代わりに、Spring Bootが標準で扱う静的リソースの場所を認証対象外にする
                .ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /**
     * Spring Securityの設定 今回はログイン画面、ログアウト画面、認証対象外のURLを設定
     *
     * @param http
     * @return
     * @throws Exception
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationManager(authenticationManager(service, encoder))
                .formLogin(login -> login
                .loginPage("/admin/login") // ログイン画面のURL
                .loginProcessingUrl("/auth") // ログイン処理をするURL
                // .loginPage("/admin/login")  // ログイン画面のURL
                .usernameParameter("username") // ログイン画面のユーザーIDのパラメータ名
                .passwordParameter("password") // ログイン画面のパスワードのパラメータ名

                // 🔽【変更点②】ログイン成功時に successHandler を使って、ユーザーのロールに応じて遷移先を振り分ける
                // これにより、ROLE_ADMIN → /admin/menu、ROLE_USER → /user/home などに自動リダイレクトされる
                .successHandler(successHandler)
                // .defaultSuccessUrl("/admin/menu") は不要になる（successHandlerが代わりに処理するため）
                .failureUrl("/admin/login?error")
                .permitAll()
                );

        // ログアウト設定
        http.logout(logout -> logout
                .logoutUrl("/logout") // ログアウト処理をするURL
                .logoutSuccessUrl("/home") // ログアウト成功時のURL
                .invalidateHttpSession(true) // ログアウト時はセッションを破棄する
                .deleteCookies("JSESSIONID") // ログアウト時はクッキーを削除する
                .clearAuthentication(true) // ログアウト時は認証情報をクリアする
                .permitAll()
        );
        // 認証設定
        http.authorizeHttpRequests(authz -> authz
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll() // エラー画面は認証対象外
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 静的リソースは認証対象外
                .requestMatchers("/home", "/admin/login", "/register").permitAll() // ログイン画面とホームは認証対象外
                // 🔽 修正点: ここにユーザー登録画面のURLを追加します
                .requestMatchers("/register").permitAll()
                // 認証済みユーザーならアクセス可能（ロール不要）
                .requestMatchers("/logout", "/session/**").authenticated()
                // ロール別アクセス制御
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                // その他のリクエストはすべて認証が必要
                .anyRequest().authenticated()
        );
        // 例外処理(アクセス拒否時の設定)
        // http.exceptionHandling(exceptions -> exceptions
        //         // アクセス拒否時のURLを指定
        //         // .accessDeniedPage("/error") 
        // );

        return http.build();
    }

    //  @Bean
    //   public PasswordEncoder passwordEncoder() {
    //       return new BCryptPasswordEncoder();
    //   }
}
