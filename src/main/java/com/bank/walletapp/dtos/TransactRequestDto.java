package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;
import lombok.Getter;

@Getter
public class TransactRequestDto {
    private Money money;
    private String username;

    public TransactRequestDto(double amount, Currency currency, String username){
        this.money = new Money(amount, currency);
        this.username = username;
    }
}
