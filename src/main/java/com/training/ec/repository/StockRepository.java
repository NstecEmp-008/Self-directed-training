package com.training.ec.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import org.springframework.stereotype.Repository;

import com.training.ec.entity.Stock;

/**
 * 株式情報を取得するためのリポジトリインターフェース
 * MyBatisに依存しないシンプルな定義
 */
@Mapper
@Repository
public interface StockRepository {
    
    /**
     * すべての銘柄を取得
     * @return 銘柄のリスト
     */
    List<Stock> findAll();
    
    /**
     * IDで特定の銘柄を検索
     * @param id 検索対象のID
     * @return 銘柄（存在しない場合はnull）
     */
    Stock findById(Long id);
    
    /**
     * 新しい銘柄を保存または既存の銘柄を更新
     * @param stock 保存対象の銘柄
     */
    void save(Stock stock);

    /**
     * IDで銘柄を削除
     * @param id 削除対象のID
     */
    void deleteById(Long id);

    /**
     * 検索条件に一致する銘柄を取得します。
     * @param searchTerm 検索キーワード（銘柄コードまたは銘柄名）
     * @return 条件に一致する銘柄のリスト
     */
    List<Stock> findBySearchTerm(@Param("searchTerm") String searchTerm);

    /**
     * 株価を更新します。
     * @param id 更新対象の銘柄ID
     * @param newPrice 新しい株価
     */
    void updateStockPrice(Stock stock);

}
