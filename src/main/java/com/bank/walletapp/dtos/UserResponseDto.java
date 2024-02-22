package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

@Data
public class UserResponseDto implements ResponseData {
    private String username;
    private BalanceResponseDto balance;
    public UserResponseDto(User user) throws WalletNotFound {
        this.username = user.getUsername();
        this.balance = new BalanceResponseDto(user.getWallet().getBalance());
    }
}

