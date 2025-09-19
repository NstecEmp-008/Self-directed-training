package com.training.ec.service;

import java.util.List;
import java.util.UUID;

import com.training.ec.entity.Stock;
import com.training.ec.entity.UserStock;

public interface TradeService {

    /**
     * 株を購入する処理
     *
     * @param userId ユーザーID
     * @param stockId 株式ID
     * @param quantity 購入株数
     */
    /**
     * 通常購入
     */
    default void buyStock(Integer userId, Long stockId, int quantity) {
        buyStock(userId, stockId, quantity, UUID.randomUUID().toString());
    }

    /**
     * 二重登録防止用のリクエストID付き購入
     */
    void buyStock(Integer userId, Long stockId, int quantity, String requestId);

    /**
     * 通常売却
     */
    default void sellStock(Integer userId, Long stockId, int quantity) {
        sellStock(userId, stockId, quantity, UUID.randomUUID().toString());
    }

    /**
     * 二重登録防止用のリクエストID付き売却
     */
    void sellStock(Integer userId, Long stockId, int quantity, String requestId);

    /**
     * ユーザーの保有株を取得
     */
    // --- 保有株一覧取得 ---
    List<UserStock> getUserStocks(Integer userId);

    /**
     * ✅ ユーザーが保有する特定の株を取得
     */
    UserStock findUserStock(Integer userId, Long stockId);

    Stock findStockById(Long stockId);

    
}
