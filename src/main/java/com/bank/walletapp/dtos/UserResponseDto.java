package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

@Data
public class UserResponseDto implements ResponseData {
    private String username;
    private WalletResponseDto wallet;
    public UserResponseDto(User user) throws WalletNotFound {
        this.username = user.getUsername();
        this.wallet = new WalletResponseDto(user.getWallet());
    }
}

