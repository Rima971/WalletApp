package com.bank.walletapp.enums;

public enum Message {
    USER_SUCCESSFUL_REGISTRATION("User is successfully registered"),
    USER_SUCCESSFUL_DELETION("User is deleted successfully"),
    USER_ALREADY_EXISTS("Username already exists"),
    USER_NOT_FOUND("No user found with the given username"),
    WALLETS_SUCCESSFUL_TRANSACTION("Transaction occurred successfully"),
    WALLET_SUCCESSFUL_DEPOSIT("Amount deposited successfully in wallet"),
    WALLET_SUCCESSFUL_WITHDRAWAL("Amount withdrawed successfully from wallet"),
    WALLET_INSUFFICIENT_FUNDS("You have insufficient funds to withdraw the given amount"),
    WALLET_UNAUTHORIZED_USER_ACTION("User is unauthorized to modify this wallet"),
    WALLET_NOT_FOUND("Wallet of the given ID doesn't exist"),
    WALLET_INVALID_TRANSACTION_RECEIVER("Wrong credentials passed for the transaction receiver"),
    MONEY_INVALID_REQUEST("Invalid amount passed"),
    ;

    public final String description;

    private Message(String description){
        this.description = description;
    }
}
