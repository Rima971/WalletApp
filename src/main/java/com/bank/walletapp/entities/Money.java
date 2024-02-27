package com.bank.walletapp.entities;

import com.bank.walletapp.clients.CurrencyConvertorClient;
import com.bank.walletapp.customValidators.ValueOfEnum;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import com.bank.walletapp.exceptions.NegativeAmountPassed;
import com.bank.walletapp.exceptions.UnsuccessfulCurrencyConversion;
import currencyConvertor.currencyConvertorRequest;
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

    public Money(double numericalValue, Currency currency) throws NegativeAmountPassed {
        if (numericalValue < 0) throw new NegativeAmountPassed();
        this.numericalValue = numericalValue;
        this.currency = currency;
    }

    public void add(Money money) throws UnsuccessfulCurrencyConversion {
        Money clone = money.clone();
        clone.convertTo(this.currency);
        this.numericalValue += clone.getNumericalValue();
    }

    public void subtract(Money money) throws UnsuccessfulCurrencyConversion {
        Money clone = money.clone();
        clone.convertTo(this.currency);
        if (clone.numericalValue > this.numericalValue) throw new InvalidAmountPassed();
        this.numericalValue -= clone.getNumericalValue();
    }

    public void convertTo(Currency currency) throws UnsuccessfulCurrencyConversion {

        if (currency != this.currency && this.numericalValue > 0){
            try {
                currencyConvertor.Money money = currencyConvertor.Money.newBuilder().setCurrency(this.currency.name()).setValue(this.numericalValue).build();
                currencyConvertorRequest request = currencyConvertorRequest.newBuilder().setMoney(money).setTargetCurrency(currency.name()).build();
                currencyConvertor.Money convertedMoney = CurrencyConvertorClient.CLIENT.convert(request);
                this.numericalValue = convertedMoney.getValue();
                this.currency = Currency.valueOf(convertedMoney.getCurrency());
            } catch (Exception e){
                throw new UnsuccessfulCurrencyConversion(e.getMessage());
            }
        } else if (this.numericalValue == 0){
            this.currency = currency;
        }
    }

    @Override
    public int compareTo(Money money) throws UnsuccessfulCurrencyConversion { // refactor
        Money clone = money.clone();
        clone.convertTo(this.currency);
        double valueInThisCurrency = clone.numericalValue;
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
