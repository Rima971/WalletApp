package com.bank.walletapp.utils;

import com.bank.walletapp.entities.GenericHttpResponse;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExceptionUtils {
    public static ResponseEntity<GenericHttpResponse> handle(Exception e){
        if (e instanceof InsufficientFunds){
            return GenericHttpResponse.create(HttpStatus.BAD_REQUEST, Message.WALLET_INSUFFICIENT_FUNDS.description, null);
        }
        if (e instanceof WalletNotFound){
            return GenericHttpResponse.create(HttpStatus.CONFLICT, Message.WALLET_NOT_FOUND.description, null);
        }
        if (e instanceof UnauthorizedWalletAction){
            return GenericHttpResponse.create(HttpStatus.UNAUTHORIZED, Message.WALLET_UNAUTHORIZED_USER_ACTION.description, null);
        }
        if (e instanceof UserNotFound){
            return GenericHttpResponse.create(HttpStatus.CONFLICT, Message.USER_NOT_FOUND.description, null);
        }
        if (e instanceof UsernameAlreadyExists){
            return GenericHttpResponse.create(HttpStatus.CONFLICT, Message.USER_ALREADY_EXISTS.description, null);
        }
        if (e instanceof InvalidTransactionReceiver){
            return GenericHttpResponse.create(HttpStatus.BAD_REQUEST, Message.WALLET_INVALID_TRANSACTION_RECEIVER.description, null);
        }
        if (e instanceof InvalidAmountPassed){
            return GenericHttpResponse.create(HttpStatus.BAD_REQUEST, Message.MONEY_INVALID_REQUEST.description, null);
        }
        return GenericHttpResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
    }
}
