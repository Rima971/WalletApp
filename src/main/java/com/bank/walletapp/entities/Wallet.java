package com.bank.walletapp.entities;

import com.bank.walletapp.enums.ServiceTax;
import com.bank.walletapp.exceptions.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallets")
@Getter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, referencedColumnName = "id")
    private User user;

    @AttributeOverrides({
            @AttributeOverride(name = "numericalValue", column = @Column(name = "BALANCE_AMOUNT", length = 5)),
            @AttributeOverride(name = "currency", column = @Column(name = "BALANCE_CURRENCY"))
    })
    private Money balance;
    public Wallet(User user){
        this.user = user;
        this.balance = new Money(0, user.getCountry().currency);
    }

    public void deposit(Money amount) {
        this.balance.add(amount);
    }

    public void withdraw(Money amount) throws InsufficientFunds {
        try {
            this.balance.subtract(amount);
        } catch (InvalidAmountPassed e){
            throw new InsufficientFunds();
        }
    }

}
