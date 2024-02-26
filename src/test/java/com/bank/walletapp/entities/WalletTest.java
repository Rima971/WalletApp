package com.bank.walletapp.entities;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InsufficientFunds;
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

    private User testIndianUser = new User(TestConstants.USER_ID, TestConstants.USERNAME, TestConstants.USERNAME, Country.INDIA);

    @BeforeEach
    public void setup(){
        openMocks(this);
    }

    @Test
    public void test_successfullyCreatingAWallet(){
        assertDoesNotThrow(()-> {
            Wallet wallet = new Wallet(this.testIndianUser);
            Money actual = wallet.getBalance();
            Money expected = new Money(0, Currency.INR);
            assertEquals(expected, actual);
        });
    }

    @Test
    public void test_successfullyDepositMoneyInSameCurrency(){
        Money amount = new Money(64, Currency.INR);

        assertDoesNotThrow(()->this.wallet.deposit(amount));
        verify(money, times(1)).add(amount);
        verify(money, never()).subtract(amount);
    }

    @Test
    public void test_successfullyWithdrawMoneyInSameCurrency(){
        Money amount = new Money(64, Currency.INR);

        assertDoesNotThrow(()->this.wallet.withdraw(amount));
        verify(money, times(1)).subtract(amount);
        verify(money, never()).add(amount);
    }

    // test transactions in different currencies

    @Test
    public void test_throwsInsufficientFundsExceptionOnAttemptingToWithdrawMoreAmountThanExistsInBalance(){
        Wallet wallet = new Wallet(this.testIndianUser);
        assertThrows(InsufficientFunds.class, ()->wallet.withdraw(new Money(100, Currency.INR)));

        wallet.deposit(new Money(210, Currency.INR));

        assertDoesNotThrow(()->wallet.withdraw(new Money(1, Currency.EURO)));
        assertThrows(InsufficientFunds.class, ()->wallet.withdraw(new Money(90, Currency.USD)));
    }

    @Test
    public void test_shouldTransactCorrectlyWithAnotherWalletInSameCurrency(){
        Money amount = new Money(10, Currency.INR);
        Money mockBalance = mock(Money.class);
        Wallet loser = spy(new Wallet(TestConstants.WALLET_ID, this.testIndianUser, mockBalance));
        Wallet gainer = spy(new Wallet(TestConstants.WALLET_ID+1, this.testIndianUser, mockBalance));

        loser.transactWith(gainer, amount);

        verify(loser, times(1)).withdraw(amount);
        verify(loser, never()).deposit(amount);
        verify(gainer, times(1)).deposit(amount);
        verify(gainer, never()).withdraw(amount);
        verify(mockBalance, times(1)).subtract(amount);
        verify(mockBalance, times(1)).add(amount);
    }
}
