package com.training.ec.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.training.ec.entity.UserStock;
import com.training.ec.repository.UserStockRepository;
import com.training.ec.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final WalletRepository walletRepository;
    private final UserStockRepository userStockRepository;

 @Override
public BigDecimal getTotalAssets(Integer userId) {
    // 1. 現金残高（null の場合は 0）
    BigDecimal balance = walletRepository.getBalance(userId);
    if (balance == null) {
        balance = BigDecimal.ZERO;
    }

    // 2. 株式評価額（保有株数 × 現在価格 の合計）
    List<UserStock> stocks = userStockRepository.findByUserId(userId);
    BigDecimal stockValue = stocks.stream()
            .map(s -> {
                if (s.getPrice() == null) return BigDecimal.ZERO;
                return s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    // 3. 合計
    return balance.add(stockValue);
}
}

