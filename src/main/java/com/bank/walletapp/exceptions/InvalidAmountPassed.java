package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

public class InvalidAmountPassed extends RuntimeException {
    public InvalidAmountPassed() {
        super(Message.MONEY_INVALID_REQUEST.description);
    }
}
