package com.bank.walletapp.entities;

import com.bank.walletapp.customValidators.ValueOfEnum;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import com.bank.walletapp.exceptions.NegativeAmountPassed;
import com.bank.walletapp.exceptions.UnsuccessfulCurrencyConversion;
import com.bank.walletapp.utils.CurrencyConversionUtils;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        money.convertTo(this.currency);
        this.numericalValue += money.getNumericalValue();
        //        this.numericalValue += money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
    }

    public void subtract(Money money) throws UnsuccessfulCurrencyConversion {
        money.convertTo(this.currency);
        if (money.numericalValue > this.numericalValue) throw new InvalidAmountPassed();
        this.numericalValue -= money.getNumericalValue();
//        this.numericalValue -= money.numericalValue * this.currency.conversionFactorToINR / money.currency.conversionFactorToINR;
    }

    public void convertTo(Currency currency) throws UnsuccessfulCurrencyConversion {
        if (currency != this.currency){
            try {
                Money convertedMoney = (new CurrencyConversionUtils()).convertCurrency(currency, this);
                this.numericalValue = convertedMoney.getNumericalValue();
                this.currency = currency;
            } catch (Exception e){
                throw new UnsuccessfulCurrencyConversion(e.getMessage());
            }

        }
    }

    @Override
    public int compareTo(Money money) throws UnsuccessfulCurrencyConversion { // refactor
        Currency originalCurrency = money.currency;
        money.convertTo(this.currency);
        double valueInThisCurrency = money.numericalValue;
        money.convertTo(originalCurrency);
        return Double.compare(this.numericalValue, valueInThisCurrency);
    }

    @Override
    public boolean equals(Object o){
        if (o == this) return true;
        if (!(o instanceof Money money)) return false;
        return money.numericalValue == this.numericalValue && money.currency == this.currency;
    }

    public boolean equalsCurrency(Money money){
        return this.currency == money.currency;
    }

}
