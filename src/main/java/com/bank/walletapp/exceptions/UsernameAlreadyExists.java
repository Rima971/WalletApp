package com.bank.walletapp.exceptions;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(){
        super("Username already exists");
    }
}
