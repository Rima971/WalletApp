package com.bank.walletapp.enums;

public enum Currency {
    INR(1), USD(0.012), EURO(0.011);

    public final double conversionFactorToINR;

    Currency(double conversionFactorToINR){
        this.conversionFactorToINR = conversionFactorToINR;
    }
}
