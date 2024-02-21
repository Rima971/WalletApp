package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InsuffiucientFunds;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
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

    @Mock
    private Wallet mockWallet;

    @InjectMocks
    private User mockUser = spy(new User(TestConstants.USERNAME, TestConstants.PASSWORD));


    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    public void test_ableToCreateWallet(){
        Wallet mockWallet = new Wallet();
        when(this.walletRepository.save(any(Wallet.class))).thenReturn(WalletServiceTest.this.mockWallet);

        assertDoesNotThrow(()->{
            Wallet savedWallet = this.walletService.createWallet();
            assertEquals(WalletServiceTest.this.mockWallet, savedWallet);
        });

    }

    @Test
    public void test_ableToGetBalanceByUsername() throws WalletNotFound {
        when(this.mockWallet.getBalance()).thenReturn(new Money(60, Currency.INR));
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));

        Money balance = this.walletService.getBalanceFromUsername(TestConstants.USERNAME);
        verify(this.mockWallet, times(1)).getBalance();
        verify(this.mockUser, times(1)).getWallet();
        Money expected = new Money(60, Currency.INR);
        assertDoesNotThrow(()->{
            assertEquals(expected, balance);
        });
    }

    @Test
    public void test_shouldThrowWalletNotFoundWhenWalletWithGivenUsernameDoesNotExist(){
        User user = new User(0, TestConstants.USERNAME, TestConstants.PASSWORD, null);
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(user));
        Money money = mock(Money.class);
        assertThrows(WalletNotFound.class, ()->this.walletService.getBalanceFromUsername(TestConstants.USERNAME));
        assertThrows(WalletNotFound.class, ()->this.walletService.deposit(TestConstants.USERNAME, money));
        assertThrows(WalletNotFound.class, ()->this.walletService.withdraw(TestConstants.USERNAME, money));
    }

    @Test
    public void test_shouldBeAbleToDepositMoney() {
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));
        Money amount = new Money(63, Currency.INR);

        assertDoesNotThrow(()->this.walletService.deposit(TestConstants.USERNAME, amount));
        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
        verify(this.mockWallet, times(1)).deposit(amount);
        verify(this.mockWallet, never()).withdraw(amount);
        verify(this.walletRepository, times(1)).save(this.mockWallet);
    }

    @Test
    public void test_shouldBeAbleToWithdrawMoney() {
        when(this.mockWallet.getBalance()).thenReturn(new Money(70, Currency.INR));
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));
        Money amount = new Money(63, Currency.INR);

        assertDoesNotThrow(()->this.walletService.withdraw(TestConstants.USERNAME, amount));
        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
        verify(mockWallet, times(1)).withdraw(amount);
        verify(mockWallet, never()).deposit(amount);
        verify(this.walletRepository).save(any(Wallet.class));
    }

    @Test
    public void test_shouldThrowInsufficientFundsExceptionOnWithdrawingExcessAmount(){
        Wallet dummyWallet = spy(new Wallet());
        User dummyUser = new User(0, TestConstants.USERNAME, TestConstants.PASSWORD, dummyWallet);
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(dummyUser));

        assertThrows(InsuffiucientFunds.class, ()->this.walletService.withdraw(TestConstants.USERNAME, new Money(10, Currency.INR)));
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
        List<User> users = new ArrayList<User>();

        users.add(spy(new User(0,null, null, spy(new Wallet()))));
        users.add(spy(new User(0,null, null, spy(new Wallet()))));
        users.add(this.mockUser);

        for (int i=0; i<users.size(); i++){
            when(this.userRepository.findByUsername(users.get(i).getUsername())).thenReturn(Optional.ofNullable(users.get(i)));
        }
        Money money = new Money(20, Currency.INR);

        this.walletService.deposit(TestConstants.USERNAME, money);

        int chosenId = users.size() - 1;

        verify(this.walletRepository, times(1)).save(users.get(chosenId).getWallet());
        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
        for (int i=0; i<users.size(); i++){
            if (i != chosenId){
                verify(users.get(i).getWallet(), never()).deposit(money);
                verify(users.get(i).getWallet(), never()).withdraw(money);
            } else {
                verify(users.get(i).getWallet(), times(1)).deposit(money);
                verify(users.get(i).getWallet(), never()).withdraw(money);
            }
        }

    }

    @Test
    public void test_shouldWithdrawFromTheCorrectWalletWhenMultipleWalletsArePresent() throws WalletNotFound {
        List<User> users = new ArrayList<User>();

        users.add(spy(new User(0,null, null, spy(new Wallet()))));
        users.add(this.mockUser);
        users.add(spy(new User(0,null, null, spy(new Wallet()))));

        for (User user : users) {
            when(this.userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        }
        Money money = new Money(20, Currency.INR);

        this.walletService.withdraw(TestConstants.USERNAME, money);

        int chosenId = users.size() - 2;

        verify(this.walletRepository, times(1)).save(users.get(chosenId).getWallet());
        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
        for (int i=0; i<users.size(); i++){
            if (i != chosenId){
                verify(users.get(i).getWallet(), never()).deposit(money);
                verify(users.get(i).getWallet(), never()).withdraw(money);
            } else {
                verify(users.get(i).getWallet(), never()).deposit(money);
                verify(users.get(i).getWallet(), times(1)).withdraw(money);
            }
        }
    }
}
