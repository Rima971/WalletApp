package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Payment;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDto implements ResponseData {
    private WalletResponseDto sender;
    private WalletResponseDto receiver;
    private LocalDateTime timestamp;
    private Money amount;

    public PaymentResponseDto(Payment payment){
        this.sender = new WalletResponseDto(payment.getSender());
        this.receiver = new WalletResponseDto(payment.getReceiver());
        this.timestamp = payment.getTimestamp();
        this.amount = payment.getAmount();
    }
}
