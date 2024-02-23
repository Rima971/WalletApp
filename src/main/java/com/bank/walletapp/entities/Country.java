package com.bank.walletapp.entities;

import com.bank.walletapp.enums.Currency;

public enum Country {
    INDIA(Currency.INR),
    USA(Currency.USD),
    UK(Currency.EURO);

    public final Currency currency;

    private Country(Currency currency){
        this.currency = currency;
    }
}
