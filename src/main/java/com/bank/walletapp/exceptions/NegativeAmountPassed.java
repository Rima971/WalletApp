package com.bank.walletapp.exceptions;

import java.security.InvalidParameterException;

public class NegativeAmountPassed extends InvalidParameterException {
    public NegativeAmountPassed(){
        super();
    }
}
