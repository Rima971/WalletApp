package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionRecordResponseDto implements ResponseData {
    private WalletResponseDto sender;
    private WalletResponseDto receiver;
    private LocalDateTime timestamp;
    private Money amount;

    public TransactionRecordResponseDto(TransactionRecord transactionRecord){
        this.sender = new WalletResponseDto(transactionRecord.getSender());
        this.receiver = new WalletResponseDto(transactionRecord.getReceiver());
        this.timestamp = transactionRecord.getTimestamp();
        this.amount = transactionRecord.getAmount();
    }
}
