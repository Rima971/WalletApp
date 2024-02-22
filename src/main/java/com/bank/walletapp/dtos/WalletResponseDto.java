package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

@Data
public class WalletResponseDto implements ResponseData {
    private int id;
    private BalanceResponseDto balance;
    public WalletResponseDto(Wallet wallet){
        this.id = wallet.getId();
        this.balance = new BalanceResponseDto(wallet.getBalance());
    }
}
