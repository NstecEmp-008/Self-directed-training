package com.training.ec.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStock {
    private Long id;
    private Integer userId;   // AccountのuserId
    private Long stockId;     // Stockのid
    private Integer quantity; // 保有株数

    // JOIN で取得する株の情報も保持
    private String symbol;
    private String name;
    private BigDecimal price;
    private LocalDateTime lastUpdated;
    private BigDecimal changePercentage; // 変動率 (%)


       // 🔽 追加（売買処理用の簡易コンストラクタ）
    public UserStock(Long id, Integer userId, Long stockId, Integer quantity, String symbol, String name, BigDecimal price) {
        this.id = id;
        this.userId = userId;
        this.stockId = stockId;
        this.quantity = quantity;
    }
}
