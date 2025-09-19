package com.training.ec.service;

import java.math.BigDecimal;


public interface PortfolioService {
 /**
     * 総資産（現金＋株式評価額）を取得
     * @param userId ユーザーID
     * @return 総資産
     */
    BigDecimal getTotalAssets(Integer userId);
   
}
