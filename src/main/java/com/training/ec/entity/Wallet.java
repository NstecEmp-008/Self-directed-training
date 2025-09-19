package com.training.ec.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private Integer userId;       // AccountのuserId
    private BigDecimal balance;   // 現在残高
}
