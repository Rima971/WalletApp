package com.bank.walletapp.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFound extends UsernameNotFoundException {
    public UserNotFound(){
        super("No user found with the given username");
    }
}
