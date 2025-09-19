package com.training.ec.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.training.ec.entity.Stock;
import com.training.ec.entity.TradeHistory;
import com.training.ec.entity.UserStock;
import com.training.ec.entity.Wallet;
import com.training.ec.repository.StockRepository;
import com.training.ec.repository.TradeRepository;
import com.training.ec.repository.UserStockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final UserStockRepository UserStockRepository;
    private final StockRepository stockRepository;

    // ユーザーIDごとのロックを管理するマップ
    // ConcurrentHashMapはスレッドセーフなMapで、複数のスレッドからの同時アクセスに対応します。
    // ReentrantLockは、同じスレッドが繰り返しロックを取得できる排他ロックです。
    private final ConcurrentHashMap<Integer, Lock> userLocks = new ConcurrentHashMap<>();

    /**
     * 株を取得する処理 追加内容
     */
    @Override
    public Stock findStockById(Long stockId) {
        Stock stock = stockRepository.findById(stockId);
                if (stock == null) {
            throw new IllegalStateException("銘柄が存在しません: id=" + stockId);
        }
        return stock;
    }

    /**
     * 株を購入する処理
     */
    @Override
    @Transactional
    public void buyStock(Integer userId, Long stockId, int quantity, String requestId) {

        // --- 🔒 二重登録防止の追加ロジック ---
        // 1. ユーザーIDに対応するロックを取得
        // この処理により、同じユーザーIDの取引が同時に実行されることを防ぎます。
        // computeIfAbsentは、マップにキー（userId）が存在しない場合のみ、新しいReentrantLockを生成してマップに格納します。
        // これにより、ユーザーごとのロックオブジェクトを効率的に管理できます。
        Lock userLock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());

        // 2. ロック開始
        // lock()メソッドは、他のスレッドがロックを解放するまで待機します。
        userLock.lock();

        try {
            // --- 💰 本来のビジネスロジック ---
            // このsynchronizedブロック内の処理は、同じユーザーIDを持つ他のスレッドからは同時に実行されません。
            // 処理中は他のスレッドはロックの解放を待つため、残高や保有株数の不整合が発生しません。

            // 3. 財布をチェック
            Wallet wallet = tradeRepository.findWalletByUserId(userId);
            if (wallet == null) {
                throw new IllegalStateException("ユーザーのウォレットが存在しません: userId=" + userId);
            }

            // 4. 株を取得
            Stock stock = tradeRepository.findStockById(stockId);
            if (stock == null) {
                throw new IllegalStateException("株が存在しません: stockId=" + stockId);
            }

            // 5. 購入金額を計算
            BigDecimal totalPrice = stock.getPrice().multiply(BigDecimal.valueOf(quantity));

            // 残高チェック
            if (wallet.getBalance().compareTo(totalPrice) < 0) {
                throw new IllegalStateException("残高不足！ 必要金額: " + totalPrice + " / 現在残高: " + wallet.getBalance());
            }

            // 6. 残高更新
            wallet.setBalance(wallet.getBalance().subtract(totalPrice));
            tradeRepository.updateWallet(wallet);

            // 7. ユーザー保有株を取得
            UserStock userStock = tradeRepository.findUserStock(userId, stockId);

            if (userStock != null) {
                // 既に保有 → 株数を追加
                userStock.setQuantity(userStock.getQuantity() + quantity);
                tradeRepository.updateUserStock(userStock);
            } else {
                // 初めての株 → 新規追加
                userStock = new UserStock(null, userId, stockId, quantity, stock.getSymbol(), stock.getName(), stock.getPrice());
                tradeRepository.insertUserStock(userStock);
            }

            // 8. 取引履歴を追加
            TradeHistory history = new TradeHistory(
                    null,
                    userId,
                    stockId,
                    quantity,
                    totalPrice,
                    "BUY",
                    LocalDateTime.now(),
                    UUID.fromString(requestId)
            );

            tradeRepository.insertTradeHistory(history);
        } finally {
            // 9. ロック解放
            // try-finallyブロックを使用することで、処理中に例外が発生しても必ずロックが解放されます。
            // これを怠ると、デッドロック（ロックされた状態が永久に続く）が発生する可能性があります。
            userLock.unlock();
        }
    }

    /**
     * 株を売却する処理
     */
    @Override
    @Transactional
    public void sellStock(Integer userId, Long stockId, int quantity, String requestId) {
        Lock userLock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
        userLock.lock();

        try {
            Wallet wallet = tradeRepository.findWalletByUserId(userId);
            Stock stock = tradeRepository.findStockById(stockId);
            UserStock userStock = tradeRepository.findUserStock(userId, stockId);

            if (userStock == null || userStock.getQuantity() < quantity) {
                throw new IllegalStateException("保有株数が不足しています: stockId=" + stockId);
            }

            // 売却金額を計算
            BigDecimal totalPrice = stock.getPrice().multiply(BigDecimal.valueOf(quantity));

            // 残高に反映
            wallet.setBalance(wallet.getBalance().add(totalPrice));
            tradeRepository.updateWallet(wallet);

            // 売却後の株数を更新
            int remaining = userStock.getQuantity() - quantity;
            if (remaining > 0) {
                userStock.setQuantity(remaining);
                tradeRepository.updateUserStock(userStock);
            } else {
                tradeRepository.deleteUserStock(userId, stockId); // 複合キーで削除
            }

            // 取引履歴を追加
            TradeHistory history = new TradeHistory(
                    null,
                    userId,
                    stockId,
                    quantity,
                    totalPrice,
                    "SELL",
                    LocalDateTime.now(),
                    UUID.fromString(requestId)
            );
            tradeRepository.insertTradeHistory(history);

        } finally {
            userLock.unlock(); // ✅ ここは1回だけ
        }
    }

    /**
     * ユーザーの保有株を取得
     */
    @Override
    public List<UserStock> getUserStocks(Integer userId) {
        return UserStockRepository.findByUserId(userId); // 元のを利用
    }

    /**
     * ✅ ユーザーが保有する特定の株を取得
     */
    @Override
    public UserStock findUserStock(Integer userId, Long stockId) {
        return tradeRepository.findUserStock(userId, stockId);
    }

    

}
