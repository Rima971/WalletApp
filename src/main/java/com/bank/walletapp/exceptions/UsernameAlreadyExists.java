package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

public class UsernameAlreadyExists extends RuntimeException {
    public UsernameAlreadyExists(){
        super(Message.USER_ALREADY_EXISTS.description);
    }
}
