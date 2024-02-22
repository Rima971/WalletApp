package com.bank.walletapp.exceptions;

import com.bank.walletapp.enums.Message;

public class UnauthorizedWalletAction extends RuntimeException {
    public UnauthorizedWalletAction(){
        super(Message.WALLET_UNAUTHORIZED_USER_ACTION.description);
    }
}
