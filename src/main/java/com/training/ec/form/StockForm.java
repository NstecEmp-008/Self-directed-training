package com.training.ec.form;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新規銘柄登録用のフォームクラス。
 * ユーザーからの入力値を保持し、バリデーションを行う。
 */
@Data
public class StockForm {

    /**
     * 銘柄コード。
     * - 空欄を許可しない
     * - 英数字のみ許可
     */
    @NotBlank(message = "銘柄コードは必須です。")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "銘柄コードは英数字のみで入力してください。")
    private String symbol;

    /**
     * 銘柄名。
     * - 空欄を許可しない
     * - 50文字以内で入力
     */
    @NotBlank(message = "銘柄名は必須です。")
    @Size(max = 50, message = "銘柄名は50文字以内で入力してください。")
    private String name;

    /**
     * 初期価格。
     * - 0以上の数値を許可
     * HTMLのinput type="number"と連携して、数値入力を促す
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "初期価格は0以上で入力してください。")
    private BigDecimal price;

    /**
     * 業種。
     * ドロップダウンリストから選択
     */
    @NotBlank(message = "業種を選択してください。")
    private String industry;

    /**
     * 在庫数。
     * - 空欄を許可しない
     * - 0以上
     */
//     @NotNull(message = "在庫数は必須です。")
//     @Min(value = 0, message = "在庫数は0以上で入力してください。")
//     private Integer quantity;
}
