package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MoneyResponseDto implements ResponseData {
    private double amount;

    private Currency currency;

    public MoneyResponseDto(Money balance){
        this.amount = balance.getNumericalValue();
        this.currency = balance.getCurrency();
    }
}
