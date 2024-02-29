package com.bank.walletapp.entities;

import com.bank.walletapp.adapters.CurrencyConvertor;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import com.bank.walletapp.exceptions.NegativeAmountPassed;
import com.bank.walletapp.exceptions.UnsuccessfulCurrencyConversion;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
public class Money implements Comparable<Money> {

    @Getter
    private double numericalValue = 0;
    @Enumerated(EnumType.STRING)
    @Getter
    private Currency currency = Currency.INR;
    @Setter
    private CurrencyConvertor currencyConvertor = new CurrencyConvertor();

    public Money(double numericalValue, Currency currency) throws NegativeAmountPassed {
        if (numericalValue < 0) throw new NegativeAmountPassed();
        this.numericalValue = numericalValue;
        this.currency = currency;
    }

    public void add(Money money) {
        Money convertedMoney = money.convertTo(this.currency);;
        this.numericalValue += convertedMoney.getNumericalValue();
    }

    public void subtract(Money money) throws InvalidAmountPassed {
        Money convertedMoney = money.convertTo(this.currency);
        if (convertedMoney.numericalValue > this.numericalValue) throw new InvalidAmountPassed();
        this.numericalValue -= convertedMoney.getNumericalValue();
    }

    private Money convertTo(Currency currency) throws UnsuccessfulCurrencyConversion {
        if (currency != this.currency && this.numericalValue > 0){
            return this.currencyConvertor.convertCurrency(this.numericalValue, this.currency, currency);
        } else if (this.numericalValue == 0){
            return new Money(this.numericalValue, currency);
        }
        return this.clone();
    }

    @Override
    public int compareTo(Money money) throws UnsuccessfulCurrencyConversion { // refactor
        Money convertedMoney = money.convertTo(this.currency);
        double valueInThisCurrency = convertedMoney.numericalValue;
        return Double.compare(this.numericalValue, valueInThisCurrency);
    }

    @Override
    public boolean equals(Object o){
        if (o == this) return true;
        if (!(o instanceof Money money)) return false;
        return money.numericalValue == this.numericalValue && money.currency == this.currency;
    }

    @Override
    public Money clone(){
        return new Money(this.getNumericalValue(), this.currency);
    }

    public boolean equalsCurrency(Money money){
        return this.currency == money.currency;
    }

}
