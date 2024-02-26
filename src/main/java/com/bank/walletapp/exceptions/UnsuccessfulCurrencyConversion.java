package com.bank.walletapp.exceptions;

public class UnsuccessfulCurrencyConversion extends RuntimeException {
    public UnsuccessfulCurrencyConversion(String message){
        super(message);
    }
}
