package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.TransactionRecord;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionRecordResponseDto {
    private String sender;
    private String receiver;
    private LocalDateTime timestamp;
    private Money amount;

    public TransactionRecordResponseDto(TransactionRecord transactionRecord){
        this.sender = transactionRecord.getSender().getUsername();
        this.receiver = transactionRecord.getReceiver().getUsername();
        this.timestamp = transactionRecord.getTimestamp();
        this.amount = transactionRecord.getAmount();
    }
}