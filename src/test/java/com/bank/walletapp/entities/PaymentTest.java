package com.bank.walletapp.entities;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.enums.ServiceTax;
import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.exceptions.InsufficientFundsForServiceFee;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;

public class PaymentTest {
    private User testIndianUser = new User(TestConstants.USER_ID, TestConstants.USERNAME, TestConstants.USERNAME, Country.INDIA);

    @Test
    public void test_shouldTransactCorrectlyWithAnotherWalletInSameCurrencyWithoutDeductingAnyServiceFee(){
        Money amount = new Money(10, Currency.INR);
        Money mockBalance = spy(new Money(20, Currency.INR));
        Wallet loser = spy(new Wallet(TestConstants.WALLET_ID, this.testIndianUser, mockBalance));
        Wallet gainer = spy(new Wallet(TestConstants.WALLET_ID+1, this.testIndianUser, mockBalance));
        new Payment(loser, gainer, amount);

        verify(loser, times(1)).withdraw(amount);
        verify(loser, never()).deposit(amount);
        verify(gainer, times(1)).deposit(amount);
        verify(gainer, never()).withdraw(amount);
        verify(mockBalance, never()).subtract(ServiceTax.CURRENCY_CONVERSION.charge);
        verify(mockBalance, never()).subtract(amount);
        verify(mockBalance, never()).add(amount);
    }

    @Test
    public void test_shouldDeductServiceFeeWhenTransactingInDifferentCurrencies(){
        Money amount = new Money(1, Currency.USD);
        Money mockBalance = spy(new Money(200, Currency.INR));
        Wallet loser = spy(new Wallet(TestConstants.WALLET_ID, this.testIndianUser, mockBalance));
        Wallet gainer = spy(new Wallet(TestConstants.WALLET_ID+1, this.testIndianUser, mockBalance));
        new Payment(loser, gainer, amount);

        verify(loser, times(1)).withdraw(amount);
        verify(loser, never()).deposit(amount);
        verify(gainer, times(1)).deposit(amount);
        verify(gainer, never()).withdraw(amount);
        verify(mockBalance, times(1)).subtract(amount);
        verify(mockBalance, times(1)).subtract(ServiceTax.CURRENCY_CONVERSION.charge);
        verify(mockBalance, times(1)).add(amount);
    }

    @Test
    public void test_shouldThrowInsufficientFundsExceptionWhenTransactionAmountExceedsBalance(){
        Money amount = new Money(10, Currency.INR);
        Money mockBalance = spy(new Money(5, Currency.INR));
        Wallet loser = new Wallet(TestConstants.WALLET_ID, this.testIndianUser, mockBalance);
        Wallet gainer = new Wallet(TestConstants.WALLET_ID+1, this.testIndianUser, mockBalance);

        assertThrows(InsufficientFunds.class, ()->new Payment(loser, gainer, amount));
    }

    @Test
    public void test_shouldThrowInsufficientFundForServiceFeeWhenServiceFeeExceedsSenderBalance(){
        Money amount = new Money(5, Currency.USD);
        Money mockBalance = spy(new Money(5, Currency.INR));
        Wallet loser = new Wallet(TestConstants.WALLET_ID, this.testIndianUser, mockBalance);
        Wallet gainer = new Wallet(TestConstants.WALLET_ID+1, this.testIndianUser, mockBalance);

        assertThrows(InsufficientFundsForServiceFee.class, ()->new Payment(loser, gainer, amount));
    }

    @Test
    public void test_shouldThrowInsufficientFundsForServiceFeeExceptionWhenServiceFeeExceedsBalanceWhileTransactionInDifferentCurrencies(){
        Money amount = new Money(5, Currency.USD);
        Money mockBalance = spy(new Money(5, Currency.INR));
        Wallet loser = new Wallet(TestConstants.WALLET_ID, this.testIndianUser, mockBalance);
        Wallet gainer = new Wallet(TestConstants.WALLET_ID+1, this.testIndianUser, mockBalance);

        assertThrows(InsufficientFundsForServiceFee.class, ()->new Payment(loser, gainer, amount));
    }
}
