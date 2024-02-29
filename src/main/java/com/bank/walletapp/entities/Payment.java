package com.bank.walletapp.entities;

import com.bank.walletapp.enums.ServiceTax;
import com.bank.walletapp.exceptions.InsufficientFundsForServiceFee;
import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import com.bank.walletapp.exceptions.UnsuccessfulCurrencyConversion;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne()
    private Wallet sender;

    @ManyToOne()
    private Wallet receiver;

    @CreatedDate
    private LocalDateTime timestamp;

    @AttributeOverrides({
            @AttributeOverride(name = "numericalValue", column = @Column(name = "transactedAmount")),
            @AttributeOverride(name = "currency", column = @Column(name = "transactedCurrency"))
    })
    private Money amount;

    public Payment(Wallet sender, Wallet receiver, Money amount){
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.transact();
    }

    private void deductCurrencyConversionServiceFee() throws InsufficientFundsForServiceFee, UnsuccessfulCurrencyConversion {
        if (!this.sender.getBalance().equalsCurrency(this.receiver.getBalance())){
            try{
                this.receiver.getBalance().subtract(ServiceTax.CURRENCY_CONVERSION.charge);
            } catch (InvalidAmountPassed e){
                throw new InsufficientFundsForServiceFee();
            }
        }

    }

    private void transact() throws InsufficientFundsForServiceFee, UnsuccessfulCurrencyConversion, InsufficientFunds {
        this.deductCurrencyConversionServiceFee();

        this.sender.withdraw(this.amount);
        this.receiver.deposit(this.amount);
    }
}
