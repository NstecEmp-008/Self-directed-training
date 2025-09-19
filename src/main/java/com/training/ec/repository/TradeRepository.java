package com.training.ec.repository;

// import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.training.ec.entity.Stock;
import com.training.ec.entity.TradeHistory;
import com.training.ec.entity.UserStock;
import com.training.ec.entity.Wallet;

@Mapper
public interface TradeRepository {

    // -------------------------------
    // 🎤 ユーザー系
    // -------------------------------
    Wallet findWalletByUserId(Integer userId);
    void updateWallet(Wallet wallet);

    // -------------------------------
    // 🎤 株式情報系
    // -------------------------------
    Stock findStockById(Long id);
    void updateStockQuantity(Stock stock);

    // -------------------------------
    // 🎤 ユーザーポートフォリオ系
    // -------------------------------
    UserStock findUserStock(@Param("userId") Integer userId, @Param("stockId") Long stockId);
    void updateUserStock(UserStock userStock);
    void insertUserStock(UserStock userStock);

    // 🔽 ここを追加
    /** 特定ユーザーの保有株一覧を取得 */
    List<UserStock> findByUserId(@Param("userId") Integer userId);

    void deleteUserStock(@Param("userId") Integer userId, @Param("stockId") Long stockId);

    // -------------------------------
    // 🎤 取引履歴系
    // -------------------------------
    void insertTradeHistory(TradeHistory history);
    List<TradeHistory> findTradeHistoryByUserId(Integer userId);

    
}


