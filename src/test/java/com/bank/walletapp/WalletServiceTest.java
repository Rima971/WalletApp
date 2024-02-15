package com.bank.walletapp;

import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InsuffiucientFunds;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.models.Money;
import com.bank.walletapp.models.Wallet;
import com.bank.walletapp.repositories.WalletRepository;
import com.bank.walletapp.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class WalletServiceTest {
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;


    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    public void test_ableToCreateWallet(){
        Wallet dummyWallet = new Wallet();
        when(this.walletRepository.save(any(Wallet.class))).thenReturn(dummyWallet);

        assertDoesNotThrow(()->{
            Wallet savedWallet = this.walletService.createWallet();
            System.out.println(savedWallet);
            assertEquals(dummyWallet, savedWallet);
        });

    }

    @Test
    public void test_ableToGetBalanceByWalletId() throws WalletNotFound {
        Wallet dummyWallet = spy(new Wallet());
        dummyWallet.deposit(new Money(60, Currency.INR));
        when(this.walletRepository.findById(any(Integer.class))).thenReturn(Optional.of(dummyWallet));

        Money balance = this.walletService.getBalanceFromId(0);
        verify(dummyWallet, times(1)).getBalance();
        Money expected = new Money(60, Currency.INR);
        assertDoesNotThrow(()->{
            assertEquals(0, expected.compareTo(balance));
        });
    }

    @Test
    public void test_shouldThrowWalletNotFoundWhenWalletWithGivenIdDoesNotExist(){
        Money money = new Money(50, Currency.INR);
        assertThrows(WalletNotFound.class, ()->this.walletService.getBalanceFromId(-1));
        assertThrows(WalletNotFound.class, ()->this.walletService.deposit(-1, money));
        assertThrows(WalletNotFound.class, ()->this.walletService.withdraw(-1, money));
    }

    @Test
    public void test_ableToDepositMoneyUsingWalletId() throws WalletNotFound {
        Wallet dummyWallet = spy(new Wallet());
        when(this.walletRepository.findById(1)).thenReturn(Optional.of(dummyWallet));
        Money amount = new Money(63, Currency.INR);

        assertDoesNotThrow(()->this.walletService.deposit(1, amount));
        verify(dummyWallet).deposit(amount);
        verify(this.walletRepository).save(any(Wallet.class));
        assertEquals(0, amount.compareTo(this.walletService.getBalanceFromId(1)));
    }

    @Test
    public void test_shouldBeAbleToWithdrawMoney() throws WalletNotFound {
        Wallet dummyWallet = spy(new Wallet());
        dummyWallet.deposit(new Money(70, Currency.INR));
        when(this.walletRepository.findById(1)).thenReturn(Optional.of(dummyWallet));
        Money amount = new Money(63, Currency.INR);

        assertDoesNotThrow(()->this.walletService.withdraw(1, amount));
        verify(dummyWallet).withdraw(amount);
        verify(this.walletRepository).save(any(Wallet.class));
        Money expected = new Money(7, Currency.INR);
        assertEquals(0, expected.compareTo(this.walletService.getBalanceFromId(1)));
    }

    @Test
    public void test_shouldThrowInsufficientFundsExceptionOnWithdrawingExcessAmount(){
        Wallet dummyWallet = spy(new Wallet());
        when(this.walletRepository.findById(1)).thenReturn(Optional.of(dummyWallet));

        assertThrows(InsuffiucientFunds.class, ()->this.walletService.withdraw(1, new Money(10, Currency.INR)));
    }

    @Test
    public void test_shouldBeAbleToDeleteAWallet() {
        when(this.walletRepository.findById(1)).thenReturn(Optional.of(new Wallet()));

        assertDoesNotThrow(() -> this.walletService.deleteWallet(1));
        verify(this.walletRepository, times(1)).deleteById(1);
    }

    @Test
    public void test_shouldBeAbleToFetchListOfWallets(){
        this.walletService.fetchAllWallets();
        verify(this.walletRepository, times(1)).findAll();
    }
}
