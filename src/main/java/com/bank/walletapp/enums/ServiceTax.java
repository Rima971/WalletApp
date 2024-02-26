package com.bank.walletapp.enums;

import com.bank.walletapp.entities.Money;

public enum ServiceTax {
    CURRENCY_CONVERSION(new Money(10, Currency.INR));

    public final Money charge;
    private ServiceTax(Money charge){
        this.charge = charge;
    }
}
