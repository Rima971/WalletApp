package com.bank.walletapp.models;

import com.bank.walletapp.exceptions.InsuffiucientFunds;
import com.bank.walletapp.exceptions.InvalidRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    public Wallet(){
        this.balance = new Money();
    }

    public void deposit(Money amount) {
//        if ((new Money()).compareTo(this.balance) == 0) this.balance = amount;
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
