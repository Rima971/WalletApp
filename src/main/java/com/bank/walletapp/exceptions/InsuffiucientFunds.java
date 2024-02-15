package com.bank.walletapp.exceptions;

import java.security.InvalidParameterException;

public class InsuffiucientFunds extends InvalidParameterException {
    public InsuffiucientFunds() {
        super("You have insufficient funds to withdraw the given amount");
    }
}
