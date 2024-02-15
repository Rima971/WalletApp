package com.bank.walletapp.exceptions;

public class InvalidRequest extends RuntimeException {
    public InvalidRequest() {
        super("Invalid amount passed");
    }
}
