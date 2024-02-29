package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

public class InsufficientFundsForServiceFee extends RuntimeException {
    public InsufficientFundsForServiceFee(){
        super(Message.TRANSACTIONS_INSUFFICIENT_AMOUNT_FOR_SERVICE_FEE.description);
    }
}
