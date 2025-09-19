package com.training.ec.service;

import java.math.BigDecimal;
import java.util.List;

import com.training.ec.entity.Stock;
import com.training.ec.form.StockForm;

/**
 * 株式情報に関するビジネスロジックを定義するサービスインターフェース。
 */
public interface StockService {

    List<Stock> getAllStocks();

    Stock getStockById(Long id);

    /**
     * 新しい銘柄を保存します（在庫数を含む）。
     */
    Stock createStock(StockForm stockForm);

    /**
     * 既存の銘柄を更新します（在庫数を含む）。
     */
    Stock updateStock(Long id, StockForm stockForm);

    void deleteStock(Long id);

    List<Stock> getStocksBySearchTerm(String searchTerm);

    // 株価更新用メソッド
    void updatePrice(Long id, BigDecimal newPrice, BigDecimal changePercentage);

}
