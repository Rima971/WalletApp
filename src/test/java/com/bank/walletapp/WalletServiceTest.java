package com.bank.walletapp;

import com.bank.walletapp.entities.User;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InsuffiucientFunds;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class WalletServiceTest {
    @Mock
    private UserRepository userRepository;
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
            assertEquals(dummyWallet, savedWallet);
        });

    }

    @Test
    public void test_ableToGetBalanceByUsername() {
        Wallet dummyWallet = spy(new Wallet());
        User dummyUser = spy(new User(TestConstants.USERNAME, TestConstants.PASSWORD, dummyWallet));
        dummyWallet.deposit(new Money(60, Currency.INR));
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(dummyUser));

        Money balance = this.walletService.getBalanceFromUsername(TestConstants.USERNAME);
        verify(dummyWallet, times(1)).getBalance();
        verify(dummyUser, times(1)).getWallet();
        Money expected = new Money(60, Currency.INR);
        assertDoesNotThrow(()->{
            assertEquals(expected, balance);
        });
    }

    @Test
    public void test_shouldThrowWalletNotFoundWhenWalletWithGivenIdDoesNotExist(){
        Money money = new Money(50, Currency.INR);
        assertThrows(WalletNotFound.class, ()->this.walletService.getBalanceFromUsername(TestConstants.USERNAME));
        assertThrows(WalletNotFound.class, ()->this.walletService.deposit(TestConstants.USERNAME, money));
        assertThrows(WalletNotFound.class, ()->this.walletService.withdraw(TestConstants.USERNAME, money));
    }

    @Test
    public void test_ableToDepositMoneyUsingWalletId() throws WalletNotFound {
        Wallet dummyWallet = spy(new Wallet());
        when(this.walletRepository.findById(1)).thenReturn(Optional.of(dummyWallet));
        Money amount = new Money(63, Currency.INR);

        assertDoesNotThrow(()->this.walletService.deposit(1, amount));
        verify(dummyWallet, times(1)).deposit(amount);
        verify(this.walletRepository, times(1)).save(any(Wallet.class));
        assertEquals(amount,this.walletService.getBalanceFromUsername(1));
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
        assertEquals(expected,this.walletService.getBalanceFromUsername(1));
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

    @Test
    public void test_shouldDepositInTheCorrectWalletWhenMultipleWalletsArePresent() throws WalletNotFound {
        List<Wallet> wallets = new ArrayList<Wallet>(List.of(spy(new Wallet()), spy(new Wallet()), spy(new Wallet())));
        for (int i=0; i<wallets.size(); i++){
            when(this.walletRepository.findById(i)).thenReturn(Optional.ofNullable(wallets.get(i)));
        }
        Money money = new Money(20, Currency.INR);

        this.walletService.deposit(2, money);

        verify(wallets.get(2), times(1)).deposit(money);
        verify(wallets.get(2), never()).withdraw(money);
        verify(this.walletRepository, times(1)).findById(2);
    }

    @Test
    public void test_shouldWithdrawFromTheCorrectWalletWhenMultipleWalletsArePresent() throws WalletNotFound {
        List<Wallet> wallets = new ArrayList<Wallet>(List.of(spy(new Wallet()), spy(new Wallet()), spy(new Wallet())));
        for (int i=0; i<wallets.size(); i++){
            wallets.get(i).deposit(new Money(20, Currency.INR));
            when(this.walletRepository.findById(i)).thenReturn(Optional.ofNullable(wallets.get(i)));
        }
        Money money = new Money(20, Currency.INR);
        this.walletService.withdraw(1, money);
        verify(wallets.get(1), times(1)).withdraw(money);
        verify(wallets.get(0), never()).withdraw(money);
        verify(wallets.get(2), never()).withdraw(money);
        verify(wallets.get(1), times(1)).deposit(money);
        verify(this.walletRepository, times(1)).findById(1);
    }
}
