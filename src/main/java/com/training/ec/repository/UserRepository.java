package com.training.ec.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.training.ec.entity.Account;
import com.training.ec.entity.AccountRole;

@Mapper
public interface UserRepository {

    // アカウント名でAccountを1件取得
    Account selectByUserName(@Param("userName") String userName);

    // ロール名でAccountRoleを1件取得
    AccountRole selectRoleByName(@Param("roleName") String roleName);

    List<AccountRole> selectRoleByName2(@Param("roleName") String roleName);

    // アカウント登録
    void insertAccount(Account account);

    // ロールIDでAccountRoleを1件取得
    AccountRole findById(@Param("roleId") Integer roleId);
    
    /**
     * 指定されたユーザーIDのロールを更新
     * @param userId 更新するユーザーのID
     * @param roleId 設定する新しいロールID
     */
    void updateUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
}
