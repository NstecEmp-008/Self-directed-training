package com.training.ec.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeHistory {
    private Long id;
    private Integer userId;
    private Long stockId;
    private Integer quantity;         // 取引株数
    private BigDecimal totalPrice;    // 取引総額
    private String tradeType;         // "BUY" or "SELL"
    private LocalDateTime tradeTime;  // 取引日時
    private java.util.UUID requestId;
}

