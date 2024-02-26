package com.bank.walletapp.utils;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;

public class CurrencyConversionUtils {

    private static class Client{
        public static Money convertCurrency(Currency currency, Money money){
            return money;
        };
    };
    public CurrencyConversionUtils(){}

    public Money convertCurrency(Currency baseCurrency, Money money) {
        return Client.convertCurrency(baseCurrency, money);
    }


}
