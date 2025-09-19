package com.training.ec.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {
    private Integer userId;
    private String userName;
    private String password;
    private AccountRole role;
}
