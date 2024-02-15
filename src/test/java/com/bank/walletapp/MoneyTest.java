package com.bank.walletapp;

import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidRequest;
import com.bank.walletapp.models.Money;
import com.bank.walletapp.models.Wallet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoneyTest {
    @Test
    public void test_throwsInvalidRequestExceptionOnAttemptingToCreateMoneyWithNegativeAmount(){
        assertThrows(InvalidRequest.class, ()->new Money(-12, Currency.INR));
        assertThrows(InvalidRequest.class, ()->new Money(-1, Currency.INR));
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

        firstMoney.add(secondMoney);
        Money expected = new Money(20.396, Currency.USD);
        assertEquals(0, expected.compareTo(firstMoney));

        secondMoney.add(thirdMoney);
        expected = new Money(33+10/0.011, Currency.INR);
        assertEquals(0, expected.compareTo(secondMoney));

        thirdMoney.add(firstMoney);
        expected = new Money(10+(20*0.011/0.012), Currency.EURO);
        System.out.println(thirdMoney+" "+expected);

        assertEquals(0, expected.compareTo(thirdMoney));
    }
}
