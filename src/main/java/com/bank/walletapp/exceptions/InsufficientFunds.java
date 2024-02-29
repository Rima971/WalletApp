package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

import java.security.InvalidParameterException;

public class InsufficientFunds extends InvalidParameterException {
    public InsufficientFunds() {
        super(Message.WALLET_INSUFFICIENT_FUNDS.description);
    }
}
