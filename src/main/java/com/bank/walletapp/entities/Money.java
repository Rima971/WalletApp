package com.bank.walletapp.entities;

import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class Money implements Comparable<Money> {

    private double numericalValue = 0;
    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.INR;

    public Money(double numericalValue, Currency currency) throws InvalidAmountPassed {
        if (numericalValue < 0) throw new InvalidAmountPassed();
        this.numericalValue = numericalValue;
        this.currency = currency;
    }

    public void add(Money money){
        this.numericalValue += money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
    }

    public void subtract(Money money) throws InvalidAmountPassed {
        if (this.compareTo(money) < 0) throw new InvalidAmountPassed();
        this.numericalValue -= money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
    }

    public void convertTo(Currency currency){
        this.numericalValue = this.numericalValue * currency.conversionFactorToINR / this.currency.conversionFactorToINR;
        this.currency = currency;
    }

    @Override
    public int compareTo(Money money) {
        double moneyValueInThisCurrency = money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
        return Double.compare(this.numericalValue, moneyValueInThisCurrency);
    }

    @Override
    public boolean equals(Object o){
        if (o == this) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return money.numericalValue == this.numericalValue && money.currency == this.currency;
    }

}
