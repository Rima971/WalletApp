package com.bank.walletapp;

import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InsuffiucientFunds;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class WalletTest {
    @Mock
    private Money money;

    @InjectMocks
    private Wallet wallet;

    @BeforeEach
    public void setup(){
        openMocks(this);
    }

    @Test
    public void test_successfullyCreatingAWallet(){
        assertDoesNotThrow(()-> {
            Wallet wallet = new Wallet();
            Money actual = wallet.getBalance();
            Money expected = new Money(0, Currency.INR);
            assertTrue(expected.compareTo(actual) == 0);

        });
    }

    @Test
    public void test_successfullyDepositMoneyInSameCurrency(){
        Money amount = new Money(64, Currency.INR);
        this.wallet.deposit(amount);
        verify(money, times(1)).add(amount);
    }

    @Test
    public void test_successfullyWithdrawMoney(){
        Money amount = new Money(64, Currency.INR);
        this.wallet.withdraw(amount);
        verify(money, times(1)).subtract(amount);
    }

    @Test
    public void test_throwsInsufficientFundsExceptionOnAttemptingToWithdrawMoreAmountThanExistsInBalance(){
        Wallet wallet = new Wallet();
        assertThrows(InsuffiucientFunds.class, ()->wallet.withdraw(new Money(100, Currency.INR)));

        wallet.deposit(new Money(210, Currency.INR));

        assertDoesNotThrow(()->wallet.withdraw(new Money(1, Currency.EURO)));
        assertThrows(InsuffiucientFunds.class, ()->wallet.withdraw(new Money(90, Currency.USD)));
    }

    @Test
    public void test_shouldTransactCorrectlyWithAnotherWallet(){
        Money amount = new Money(10, Currency.INR);
        Money mockbalance = mock(new Money(20, Currency.INR));
        Wallet loser = spy(new Wallet(0, mockbalance, true));
        Wallet gainer = spy(new Wallet(0, mockbalance, true));

        loser.transactWith(gainer, amount);

        verify(loser, times(1)).withdraw(amount);
        verify(gainer, times(1)).deposit(amount);
        verify(mockbalance, times(1)).subtract(amount);
        verify(mockbalance, times(1)).add(amount);
    }
}
