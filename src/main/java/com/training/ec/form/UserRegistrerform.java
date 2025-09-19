package com.training.ec.form;

import jakarta.validation.constraints.AssertTrue; // AssertTrueをインポート
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 他のLombokアノテーションは省略

@Data
public class UserRegistrerform {

    @NotBlank(message = "ユーザー名は必須です")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数字のみ")
    private String username;

    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "8文字以上で入力してください")
    private String password;

    @NotBlank(message = "パスワード確認は必須です")
    private String confirmPassword;

    // パスワード一致チェックのロジック
    // このメソッドがtrueを返さない場合、バリデーションエラーとなる
    @AssertTrue(message = "パスワードが一致しません")
    public boolean isPasswordConfirmed() {
        // passwordとconfirmPasswordの両方がnullでない場合のみ比較する
        if (password == null || confirmPassword == null) {
            return true; // 片方または両方がnullの場合は、@NotBlankでチェックされるのでここでは常にtrueを返す
        }
        return password.equals(confirmPassword);
    }
}