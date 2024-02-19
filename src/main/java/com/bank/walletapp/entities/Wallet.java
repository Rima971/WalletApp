package com.bank.walletapp.entities;

import com.bank.walletapp.exceptions.InsuffiucientFunds;
import com.bank.walletapp.exceptions.InvalidRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Money balance;
    @JsonIgnore
    private boolean depositedBefore = false;

    public Wallet(){
        this.balance = new Money();
    }

    public void deposit(Money amount) {
        if (!this.depositedBefore) { // convert balance money to the currency of the first deposit
            this.balance.convertTo(amount.getCurrency());
            this.depositedBefore = true;
        };
        this.balance.add(amount);
    }

    public void withdraw(Money amount) throws InsuffiucientFunds {
        try {
            this.balance.subtract(amount);
        } catch (InvalidRequest e){
            throw new InsuffiucientFunds();
        }
    }

}
