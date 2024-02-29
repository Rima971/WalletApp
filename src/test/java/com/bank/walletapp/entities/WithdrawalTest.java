package com.bank.walletapp.entities;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.adapters.CurrencyConvertor;
import com.bank.walletapp.enums.Currency;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public class WithdrawalTest {
    @Test
    public void test_shouldWithdrawMoneyInSameCurrency(){
        Money balance = spy(new Money(10, Currency.INR));
        Money amount = new Money(2, Currency.INR);
        Wallet wallet = spy(new Wallet(TestConstants.WALLET_ID, new User(), balance));
        new Withdrawal(wallet, amount);
        verify(wallet, times(1)).withdraw(amount);
        verify(wallet, never()).deposit(amount);
        verify(balance, times(1)).subtract(amount);
        verify(balance, never()).add(amount);
    }

    @Test
    public void test_shouldWithdrawMoneyInDifferentCurrency(){
        CurrencyConvertor mockConvertor = mock(CurrencyConvertor.class);
        when(mockConvertor.convertCurrency(2, Currency.INR, Currency.USD)).thenReturn(new Money(2, Currency.USD));
        Money balance = spy(new Money(10, Currency.USD));
        Money amount = new Money(2, Currency.INR);
        amount.setCurrencyConvertor(mockConvertor);
        Wallet wallet = spy(new Wallet(TestConstants.WALLET_ID, new User(), balance));

        new Withdrawal(wallet, amount);

        verify(mockConvertor, times(1)).convertCurrency(2, Currency.INR, Currency.USD);
        verify(wallet, times(1)).withdraw(amount);
        verify(wallet, never()).deposit(amount);
        verify(balance, times(1)).subtract(amount);
        verify(balance, never()).add(amount);
    }
}
