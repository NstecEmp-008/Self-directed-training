package com.training.ec.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AccountUserDetails implements UserDetails {
    
    /** ユーザーアカウント情報 */
    private final Account account;

    /** ユーザーアカウント権限情報 */
    private final Collection<GrantedAuthority> authorities;

    /** 
     * コンストラクタ
     * @param account ユーザーアカウント情報
     * @param authorities ユーザーアカウント権限情報
     */
    public AccountUserDetails(Account account, Collection<GrantedAuthority> authorities) {
        this.account = account;
        this.authorities = authorities;
    }

    /** 権限リストを返す */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /** パスワードを返す */
    @Override
    public String getPassword() {
        return account.getPassword();
    }

    /** ユーザー名を返す */
    @Override
    public String getUsername() {
        return account.getUserName();
    }

    /** アカウントの有効期限チェック */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** アカウントロックチェック */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** 認証情報の有効期限チェック */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** アカウント有効フラグ */
    @Override
    public boolean isEnabled() {
        return true;
    }

    // ✅ 追加: ユーザーIDを取得できるようにする
    public Integer getUserId() {
        return account.getUserId();
    }

    // ✅ 追加: Account全体を返すgetter（必要なら）
    public Account getAccount() {
        return account;
    }
}
