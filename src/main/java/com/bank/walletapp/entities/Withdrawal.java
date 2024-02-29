package com.bank.walletapp.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "withrawal")
@Data
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne()
    private final Wallet wallet;

    private final Money amount;

    public Withdrawal(Wallet wallet, Money amount){
        this.wallet = wallet;
        this.amount = amount;
        this.transact();
    }

    private void transact(){
        this.wallet.withdraw(amount);
    }
}
