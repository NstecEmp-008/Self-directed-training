package com.training.ec.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.training.ec.entity.Stock;
import com.training.ec.entity.UserStock;
import com.training.ec.repository.StockRepository;
import com.training.ec.repository.UserStockRepository;
import com.training.ec.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockPriceSimulationServiceImpl implements StockPriceSimulationService {

    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository; // ✅ 追加
    private final WalletRepository walletRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    /**
     * 3秒ごとに株価をランダムに更新 (-5% ～ +5%)
     */
    @Override
    @Scheduled(fixedRate = 3000)
    public void updateStockPrices() {
        List<Stock> stocks = stockRepository.findAll();

        for (Stock stock : stocks) {
            // -5% ~ +5% のランダム変動率
            double changeRate = -0.05 + (0.10 * random.nextDouble());

            // 新しい株価を計算
            BigDecimal change = stock.getPrice()
                    .multiply(BigDecimal.valueOf(changeRate))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal newPrice = stock.getPrice().add(change);

            // 株価が0円未満にならないよう制御
            if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                newPrice = BigDecimal.valueOf(1.00);
            }

            // 前日比を % で計算
            BigDecimal changePercentage = BigDecimal.valueOf(changeRate * 100)
                    .setScale(2, RoundingMode.HALF_UP);

            // DB更新
            stockRepository.updateStockPrice(
                    new Stock(stock.getId(), stock.getSymbol(), stock.getName(),
                            newPrice, changePercentage, stock.getLastUpdated(), stock.getQuantity())
            );

            // Entity の価格も更新（WebSocket送信用）
            stock.setPrice(newPrice);
            stock.setChangePercentage(changePercentage);
        }

        // WebSocketで更新情報を送信（画面にリアルタイム反映）
        messagingTemplate.convertAndSend("/topic/stocks", stocks);

        // ✅ 各銘柄ごとにも個別トピックで送信（売却画面や購入画面用）
        for (Stock stock : stocks) {
            messagingTemplate.convertAndSend("/topic/stock/" + stock.getId(), stock);
        }

        // ✅ 各ユーザーごとの総資産を計算して送信
        List<Integer> userIds = walletRepository.findAllUserIds(); // 追加で用意
        // StockPriceSimulationServiceImpl の中で
        for (Integer userId : userIds) {
            BigDecimal balance = walletRepository.getBalance(userId);
            if (balance == null) {
                balance = BigDecimal.ZERO;
            }

            List<UserStock> userStocks = userStockRepository.findByUserId(userId);
            BigDecimal stockValue = userStocks.stream()
                    .map(s -> s.getPrice().multiply(BigDecimal.valueOf(s.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalAssets = balance.add(stockValue);

            // ✅ Entity をまとめて送る代わりに Map にする
            Map<String, Object> portfolioData = new HashMap<>();
            portfolioData.put("cashBalance", balance);
            portfolioData.put("stockValue", stockValue);
            portfolioData.put("totalAssets", totalAssets);

            messagingTemplate.convertAndSend("/topic/portfolio/assets/" + userId, portfolioData);
        }

        System.out.println("✅ 株価更新しました！ 全ユーザーの総資産を送信");
    }
}
