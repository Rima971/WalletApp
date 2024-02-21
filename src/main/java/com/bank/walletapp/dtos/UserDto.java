package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.exceptions.WalletNotFound;
import lombok.Data;

@Data
public class UserDto {
    private String username;
    private Money balance;
    public UserDto(User user) throws WalletNotFound {
        this.username = user.getUsername();
        this.balance = user.getWallet().getBalance();
    }
}
