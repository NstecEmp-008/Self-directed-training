package com.training.ec.repository;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.training.ec.entity.Wallet;

@Mapper
@Repository
public interface WalletRepository {

    BigDecimal findCashBalanceByUserId(Integer userId);

    BigDecimal findStockValuationByUserId(@Param("userId") Integer userId);

    BigDecimal getBalance(@Param("userId") Integer userId);
    // ✅ 全ユーザーのIDを取得するクエリを用意

    List<Integer> findAllUserIds();

    void insertWallet(Wallet wallet);

}
