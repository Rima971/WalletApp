package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

import java.security.InvalidParameterException;

public class InvalidWalletPassed extends InvalidParameterException {
    public InvalidWalletPassed(){
        super(Message.USER_INVALID_WALLET_PASSED.description);
    }
}
