package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

public class InvalidRequest extends RuntimeException {
    public InvalidRequest() {
        super(Message.MONEY_INVALID_REQUEST.description);
    }
}
