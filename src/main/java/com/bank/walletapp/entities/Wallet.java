package com.bank.walletapp.entities;

import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.enums.ServiceTax;
import com.bank.walletapp.exceptions.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private void deductCurrencyConversionServiceFee(Money money) throws InsufficientFundForServiceFee, UnsuccessfulCurrencyConversion {
        if (!money.equalsCurrency(this.balance)){
            try{
                money.subtract(ServiceTax.CURRENCY_CONVERSION.charge);
            } catch (InvalidAmountPassed e){
                throw new InsufficientFundForServiceFee();
            }
        }

    }

    public void deposit(Money amount) throws UnsuccessfulCurrencyConversion, InsufficientFundForServiceFee {
        this.deductCurrencyConversionServiceFee(amount);

        this.balance.add(amount);
    }

    public void withdraw(Money amount) throws InsufficientFunds, UnsuccessfulCurrencyConversion, InsufficientFundForServiceFee {
        this.deductCurrencyConversionServiceFee(amount);

        try {
            this.balance.subtract(amount);
        } catch (InvalidAmountPassed e){
            throw new InsufficientFunds();
        }
    }

    public void transactWith(Wallet wallet, Money amount) throws InsufficientFunds, UnsuccessfulCurrencyConversion, InsufficientFundForServiceFee {
        this.deductCurrencyConversionServiceFee(amount);

        this.withdraw(amount);
        wallet.deposit(amount);
    }

}
