package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

@Data
public class BalanceResponseDto implements ResponseData {
    private double amount;
    private Currency currency;

    public BalanceResponseDto(Money balance){
        this.amount = balance.getNumericalValue();
        this.currency = balance.getCurrency();
    }
}
