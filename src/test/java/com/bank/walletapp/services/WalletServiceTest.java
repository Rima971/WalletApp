package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.entities.*;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
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
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;


    private User mockUser = spy(new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA));


    private Wallet wallet = new Wallet(this.mockUser);
    @BeforeEach
    void setup(){
        openMocks(this);
        when(this.wallet.getId()).thenReturn(TestConstants.WALLET_ID);
    }

    @Test
    public void test_shouldBeAbleToAddAWallet(){
        when(this.walletRepository.save(any(Wallet.class))).thenReturn(WalletServiceTest.this.wallet);

        assertDoesNotThrow(()->{
            Wallet savedWallet = this.walletService.create(TestConstants.USERNAME);
            assertEquals(WalletServiceTest.this.wallet, savedWallet);
        });

    }

    @Test
    public void test_ableToGetBalanceById() throws WalletNotFound {
        when(this.wallet.getBalance()).thenReturn(new Money(60, Currency.INR));
//        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));

        Money balance = this.walletService.fetchBalanceFromId(TestConstants.USERNAME, TestConstants.WALLET_ID);
        verify(this.wallet, times(1)).getBalance();
        Money expected = new Money(60, Currency.INR);
        assertDoesNotThrow(()->{
            assertEquals(expected, balance);
        });
    }

    @Test
    public void test_shouldThrowWalletNotFoundWhenWalletWithGivenUsernameDoesNotExist(){
        User user = new User(TestConstants.USER_ID, TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(user));
        Money money = mock(Money.class);
        assertThrows(WalletNotFound.class, ()->this.walletService.fetchBalanceFromId(TestConstants.USERNAME, TestConstants.WALLET_ID));
    }







    @Test
    public void test_shouldBeAbleToDeleteAWallet() {
        assertDoesNotThrow(() -> this.walletService.deleteAllByUsername(TestConstants.USERNAME));
        verify(this.walletRepository, times(1)).deleteAllByUserUsername(TestConstants.USERNAME);
    }

//    @Test
//    public void test_shouldBeAbleToFetchListOfWallets(){
//        this.walletService.fetchAllWallets();
//        verify(this.walletRepository, times(1)).findAll();
//    }

//    @Test
//    public void test_shouldDepositInTheCorrectWalletWhenMultipleWalletsArePresent() throws WalletNotFound {
//        List<User> users = new ArrayList<User>();
//
//        users.add(spy(new User(0,null, null, null, spy(new Wallet()))));
//        users.add(spy(new User(0,null, null, null, spy(new Wallet()))));
//        users.add(this.mockUser);
//
//        for (int i=0; i<users.size(); i++){
//            when(this.userRepository.findByUsername(users.get(i).getUsername())).thenReturn(Optional.ofNullable(users.get(i)));
//        }
//        Money money = new Money(20, Currency.INR);
//
//        this.walletService.deposit(TestConstants.USERNAME, TestConstants.WALLET_ID, money);
//
//        int chosenId = users.size() - 1;
//
//        verify(this.walletRepository, times(1)).save(users.get(chosenId).getWallet());
//        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
//        for (int i=0; i<users.size(); i++){
//            if (i != chosenId){
//                verify(users.get(i).getWallet(), never()).deposit(money);
//                verify(users.get(i).getWallet(), never()).withdraw(money);
//            } else {
//                verify(users.get(i).getWallet(), times(1)).deposit(money);
//                verify(users.get(i).getWallet(), never()).withdraw(money);
//            }
//        }
//
//    }
//
//    @Test
//    public void test_shouldWithdrawFromTheCorrectWalletWhenMultipleWalletsArePresent() throws WalletNotFound {
//        List<User> users = new ArrayList<User>();
//        users.add(spy(new User(0,null, null, null, spy(new Wallet()))));
//        users.add(this.mockUser);
//        users.add(spy(new User(0,null, null, null, spy(new Wallet()))));
//
//        for (User user : users) {
//            when(this.userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
//        }
//        Money money = new Money(20, Currency.INR);
//
//        this.walletService.withdraw(TestConstants.USERNAME, TestConstants.WALLET_ID, money);
//
//        int chosenId = users.size() - 2;
//
//        verify(this.walletRepository, times(1)).save(users.get(chosenId).getWallet());
//        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
//        for (int i=0; i<users.size(); i++){
//            if (i != chosenId){
//                verify(users.get(i).getWallet(), never()).deposit(money);
//                verify(users.get(i).getWallet(), never()).withdraw(money);
//            } else {
//                verify(users.get(i).getWallet(), never()).deposit(money);
//                verify(users.get(i).getWallet(), times(1)).withdraw(money);
//            }
//        }
//    }
//
//    @Test
//    public void test_shouldTransactMoneyBetweenWallets() throws WalletNotFound {
//        Wallet senderWallet = spy(new Wallet(TestConstants.WALLET_ID, new Money(30, Currency.INR)));
//        User sender = new User(TestConstants.USER_ID, TestConstants.TRANSACTION_SENDER_USERNAME, TestConstants.PASSWORD, Country.INDIA, senderWallet);
//        Wallet receiverWallet = spy(new Wallet(TestConstants.WALLET_ID, new Money()));
//        User receiver = new User(TestConstants.USER_ID, TestConstants.TRANSACTION_RECEIVER_USERNAME, TestConstants.PASSWORD, Country.INDIA, receiverWallet);
//        Money moneyToTransact = new Money(20, Currency.INR);
//
//        when(this.userRepository.findByUsername(TestConstants.TRANSACTION_SENDER_USERNAME)).thenReturn(Optional.of(sender));
//        when(this.userRepository.findByUsername(TestConstants.TRANSACTION_RECEIVER_USERNAME)).thenReturn(Optional.of(receiver));
//
//        assertDoesNotThrow(()->this.walletService.transact(TestConstants.WALLET_ID, TestConstants.TRANSACTION_SENDER_USERNAME, TestConstants.TRANSACTION_RECEIVER_USERNAME, moneyToTransact));
//        assertEquals(new Money(10, Currency.INR), sender.getWallet().getBalance());
//        assertEquals(new Money(20, Currency.INR), receiver.getWallet().getBalance());
//    }
//
//    @Test
//    public void test_shouldThrowInsufficientBalanceWhenTransactionAmountExceedsSenderBalance() {
//        Wallet senderWallet = new Wallet(TestConstants.WALLET_ID, new Money());
//        senderWallet.deposit(new Money(10, Currency.INR));
//        User sender = new User(TestConstants.USER_ID, TestConstants.TRANSACTION_SENDER_USERNAME, TestConstants.PASSWORD, Country.INDIA, senderWallet);
//        User receiver = new User(TestConstants.USER_ID, TestConstants.TRANSACTION_RECEIVER_USERNAME, TestConstants.PASSWORD, Country.INDIA, new Wallet());
//        Money moneyToTransact = new Money(20, Currency.INR);
//
//        when(this.userRepository.findByUsername(TestConstants.TRANSACTION_SENDER_USERNAME)).thenReturn(Optional.of(sender));
//        when(this.userRepository.findByUsername(TestConstants.TRANSACTION_RECEIVER_USERNAME)).thenReturn(Optional.of(receiver));
//
//        assertThrows(InsufficientFunds.class, ()->this.walletService.transact(TestConstants.WALLET_ID, TestConstants.TRANSACTION_SENDER_USERNAME, TestConstants.TRANSACTION_RECEIVER_USERNAME, moneyToTransact));
//    }
//
//    @Test
//    public void test_shouldThrowInvalidTransactionReceiverWhenTheTransactionOccursBetweenTheSameUser() {
//        Wallet senderWallet = new Wallet(TestConstants.WALLET_ID, new Money());
//        senderWallet.deposit(new Money(10, Currency.INR));
//        User sender = new User(TestConstants.USER_ID, TestConstants.TRANSACTION_SENDER_USERNAME, TestConstants.PASSWORD, Country.INDIA, senderWallet);
//
//        when(this.userRepository.findByUsername(TestConstants.TRANSACTION_SENDER_USERNAME)).thenReturn(Optional.of(sender));
//
//        assertThrows(InvalidTransactionReceiver.class, ()->this.walletService.transact(TestConstants.WALLET_ID, TestConstants.TRANSACTION_SENDER_USERNAME, TestConstants.TRANSACTION_SENDER_USERNAME, new Money()));
//    }

}
