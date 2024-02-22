package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFound extends UsernameNotFoundException {
    public UserNotFound(){
        super(Message.USER_NOT_FOUND.description);
    }
}
