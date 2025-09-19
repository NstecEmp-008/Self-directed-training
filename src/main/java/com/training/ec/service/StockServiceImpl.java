package com.training.ec.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.training.ec.entity.Stock;
import com.training.ec.form.StockForm;
import com.training.ec.repository.StockRepository;

/**
 * 株式情報に関するビジネスロジックを担当するサービスクラスです。 StockServiceインターフェースを実装します。
 */
@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<Stock> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        stocks.sort(Comparator.comparing(Stock::getId)); // IDで昇順ソート
        return stocks;
    }

    @Override
    public Stock getStockById(Long id) {
        return stockRepository.findById(id);
    }

    @Override
    @Transactional
    public Stock createStock(StockForm stockForm) {
        Stock newStock = new Stock();
        newStock.setSymbol(stockForm.getSymbol());
        newStock.setName(stockForm.getName());
        newStock.setPrice(stockForm.getPrice());
        newStock.setChangePercentage(BigDecimal.ZERO); // 初期値は0%
        newStock.setLastUpdated(LocalDateTime.now());
        // newStock.setQuantity(stockForm.getQuantity()); // 在庫数を設定

        stockRepository.save(newStock);
        return newStock;
    }

    @Override
    @Transactional
    public Stock updateStock(Long id, StockForm stockForm) {
        Stock existingStock = stockRepository.findById(id);

        if (existingStock == null) {
            return null; // 更新対象が存在しない場合
        }

        existingStock.setSymbol(stockForm.getSymbol());
        existingStock.setName(stockForm.getName());
        existingStock.setPrice(stockForm.getPrice());
        // existingStock.setQuantity(stockForm.getQuantity()); // 在庫数も更新
        existingStock.setLastUpdated(LocalDateTime.now());

        stockRepository.save(existingStock);
        return existingStock;
    }

    @Override
    @Transactional
    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }

    @Override
    public List<Stock> getStocksBySearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return stockRepository.findAll();
        }
        return stockRepository.findBySearchTerm(searchTerm);
    }

    @Override
    @Transactional
    public void updatePrice(Long id, BigDecimal newPrice, BigDecimal changePercentage) {
        Stock stock = stockRepository.findById(id);
        if (stock != null) {
            stock.setPrice(newPrice);
            stock.setLastUpdated(LocalDateTime.now());
            stockRepository.updateStockPrice(stock);
        }
    }

}
