package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

@Data
public class WalletResponseDto implements ResponseData {
    private int walletId;
    private MoneyResponseDto balance;
    private UserResponseDto owner;
    public WalletResponseDto(Wallet wallet) {
        this.walletId = wallet.getId();
        this.balance = new MoneyResponseDto(wallet.getBalance());
        this.owner = new UserResponseDto(wallet.getUser());
    }
}
