package com.bank.walletapp.entities;

import com.bank.walletapp.adapters.CurrencyConvertor;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import com.bank.walletapp.exceptions.NegativeAmountPassed;
import org.hibernate.annotations.JdbcType;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.SQLType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class MoneyTest {
    private CurrencyConvertor currencyConvertor = mock(CurrencyConvertor.class);

    @Test
    public void test_throwsNegativeAmountPassedExceptionOnAttemptingToCreateMoneyWithNegativeAmount(){
        assertThrows(NegativeAmountPassed.class, ()->new Money(-12, Currency.INR));
        assertThrows(NegativeAmountPassed.class, ()->new Money(-1, Currency.INR));
    }

    @Test
    public void test_shouldCorrectlyAddTwoAmountsWithSameCurrency(){
        Money firstMoney = new Money(20, Currency.USD);
        Money secondMoney = new Money(33, Currency.USD);
        firstMoney.add(secondMoney);
        Money expected = new Money(53, Currency.USD);
        assertEquals(0, expected.compareTo(firstMoney));

        secondMoney.add(firstMoney);
        Money nextExpected = new Money(86, Currency.USD);
        assertEquals(0, nextExpected.compareTo(secondMoney));
    }

    @Test
    public void test_shouldCorrectlyAddAmountsWithDifferentCurrencies_retainCurrencyOfFirstAmount(){
        Money firstMoney = new Money(20, Currency.USD);
        Money secondMoney = new Money(33, Currency.INR);
        Money thirdMoney = new Money(10, Currency.EURO);
        firstMoney.setCurrencyConvertor(this.currencyConvertor);
        secondMoney.setCurrencyConvertor(this.currencyConvertor);
        thirdMoney.setCurrencyConvertor(this.currencyConvertor);

        when(this.currencyConvertor.convertCurrency(33, Currency.INR, Currency.USD)).thenReturn(new Money(0.40, Currency.USD));

        firstMoney.add(secondMoney);
        verify(this.currencyConvertor, times(1)).convertCurrency(33, Currency.INR, Currency.USD);
        Money expected = new Money(20.40, Currency.USD);
        assertEquals(expected, firstMoney);

        when(this.currencyConvertor.convertCurrency(10, Currency.EURO, Currency.INR)).thenReturn(new Money(909.09, Currency.INR));

        secondMoney.add(thirdMoney);
        System.out.println(secondMoney.getNumericalValue());
        expected = new Money(942.09, Currency.INR);
        assertEquals(expected, secondMoney);

        when(this.currencyConvertor.convertCurrency(20.40, Currency.USD, Currency.EURO)).thenReturn(new Money(18.70, Currency.INR));

        thirdMoney.add(firstMoney);
        expected = new Money(28.70, Currency.EURO);
        assertEquals(expected, thirdMoney);
    }

    @Test
    public void test_shouldCorrectlySubtractTwoAmountsWithSameCurrency(){
        Money firstMoney = new Money(40, Currency.USD);
        Money secondMoney = new Money(33, Currency.USD);
        firstMoney.subtract(secondMoney);
        Money expected = new Money(7, Currency.USD);
        assertEquals(0, expected.compareTo(firstMoney));
    }

    @Test
    public void test_shouldCorrectlySubtractAmountsWithDifferentCurrencies_retainCurrencyOfFirstAmount(){
        Money firstMoney = new Money(20, Currency.USD);
        Money secondMoney = new Money(33, Currency.INR);
        Money thirdMoney = new Money(10, Currency.EURO);
        firstMoney.setCurrencyConvertor(this.currencyConvertor);
        secondMoney.setCurrencyConvertor(this.currencyConvertor);
        thirdMoney.setCurrencyConvertor(this.currencyConvertor);

        when(this.currencyConvertor.convertCurrency(33, Currency.INR, Currency.USD)).thenReturn(new Money(0.40, Currency.USD));

        firstMoney.subtract(secondMoney);
        verify(this.currencyConvertor, times(1)).convertCurrency(33, Currency.INR, Currency.USD);
        Money expected = new Money(19.60, Currency.USD);
        assertEquals(expected, firstMoney);

        when(this.currencyConvertor.convertCurrency(10, Currency.EURO, Currency.INR)).thenReturn(new Money(909.09, Currency.INR));

        thirdMoney.subtract(secondMoney);
        expected = new Money(899.09, Currency.INR);
        assertEquals(expected, secondMoney);

        when(this.currencyConvertor.convertCurrency(899.09, Currency.INR, Currency.EURO)).thenReturn(new Money(9.89, Currency.EURO));

        thirdMoney.subtract(secondMoney);
        expected = new Money(0.11, Currency.EURO);
        assertEquals(expected, thirdMoney);
    }

    @Test
    public void test_shouldThrowInvalidRequestExceptionOnAttemptingToSubtractBiggerMoneyFromSmallerOne(){
        Money firstMoney = new Money(30, Currency.USD);
        Money secondMoney = new Money(33, Currency.USD);
        assertThrows(InvalidAmountPassed.class, ()->firstMoney.subtract(secondMoney));
    }
}
