package com.bank.walletapp.models;

import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidRequest;
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

    public Money(double numericalValue, Currency currency) throws InvalidRequest {
        if (numericalValue < 0) throw new InvalidRequest();
        this.numericalValue = numericalValue;
        this.currency = currency;
    }

    public void add(Money money){
        this.numericalValue += money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
    }

    public void subtract(Money money) throws InvalidRequest{
        if (this.compareTo(money) < 0) throw new InvalidRequest();
        this.numericalValue -= money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
    }

    @Override
    public int compareTo(Money money) {
        double moneyValueInThisCurrency = money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
        System.out.println(moneyValueInThisCurrency);
        return Double.compare(this.numericalValue, moneyValueInThisCurrency);
    }

}
