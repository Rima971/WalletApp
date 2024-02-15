package com.bank.walletapp.exceptions;

public class WalletNotFound extends Exception{
    public WalletNotFound(){
        super("Wallet of the given ID doesn't exist");
    }
}
