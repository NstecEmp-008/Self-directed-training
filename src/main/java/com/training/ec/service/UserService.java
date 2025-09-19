package com.training.ec.service;

import com.training.ec.form.UserRegistrerform;

/**
 * ユーザー登録・管理に関するビジネスロジックのインターフェース
 */
public interface UserService {

    /**
     * ユーザー登録処理
     *
     * @param form ユーザー登録フォームの入力値
     */
    void register(UserRegistrerform form);

    /**
     * 指定されたユーザーIDのロールを更新するメソッド
     *
     * @param userId 更新するユーザーのID
     * @param newRoleName 新しいロール名
     */
    void updateUserRole(Integer userId, String newRoleName);

    
}
