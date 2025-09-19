package com.training.ec.repository;

import org.apache.ibatis.annotations.Mapper;

import com.training.ec.entity.Account;
@Mapper
public interface AccountRepository {
    void insertAccount(Account account); // アカウント登録

    void insert(Account account);

    Account selectByUserName(String username);
    // @Param("username") String username

    Account selectById(String accountId); // IDでアカウントを取得
}
