package com.bank.walletapp.entities;

import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.exceptions.InvalidRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@AllArgsConstructor
@Table(name = "wallets")
@Getter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @AttributeOverrides({
            @AttributeOverride(name = "numericalValue", column = @Column(name = "BALANCE_AMOUNT", length = 5)),
            @AttributeOverride(name = "currency", column = @Column(name = "BALANCE_CURRENCY"))
    })
    private Money balance;
    public Wallet(){
        this.balance = new Money();
    }

    public void deposit(Money amount) {
        this.balance.add(amount);
    }

    public void withdraw(Money amount) throws InsufficientFunds {
        try {
            this.balance.subtract(amount);
        } catch (InvalidRequest e){
            throw new InsufficientFunds();
        }
    }

    public void transactWith(Wallet wallet, Money amount) throws InvalidRequest, InsufficientFunds {
        this.withdraw(amount);
        wallet.deposit(amount);
    }

}
