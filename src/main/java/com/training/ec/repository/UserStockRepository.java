package com.training.ec.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.training.ec.entity.UserStock;

@Mapper
public interface UserStockRepository {
    List<UserStock> findByUserId(Integer userId);
    UserStock findUserStock(@Param("userId") Integer userId, @Param("stockId")Long stockId);
    void insert(UserStock userStock);
    void update(UserStock userStock);
    void delete(Long id);
}

