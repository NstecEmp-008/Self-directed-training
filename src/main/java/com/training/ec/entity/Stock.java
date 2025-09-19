package com.training.ec.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 株式情報を表すJPAエンティティクラス
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    private Long id;
    private String symbol; // 銘柄コード
    private String name; // 銘柄名
    private BigDecimal price; // 現在価格
    private BigDecimal changePercentage; // 前日比（パーセント）
    private LocalDateTime lastUpdated; // 最終更新日時
    private Integer quantity;         // ← 在庫数を追加

}
