package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

public class WalletNotFound extends Exception{
    public WalletNotFound(){
        super(Message.WALLET_NOT_FOUND.description);
    }
}
