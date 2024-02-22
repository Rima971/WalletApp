package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

public class InvalidTransactionReceiver extends RuntimeException {
    public InvalidTransactionReceiver(){
        super(Message.WALLET_INVALID_TRANSACTION_RECEIVER.description);
    }
}
