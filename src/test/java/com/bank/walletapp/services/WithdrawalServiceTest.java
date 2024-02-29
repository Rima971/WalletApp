package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.entities.Country;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class WithdrawalServiceTest {
//    @Test
//    public void test_shouldBeAbleToWithdrawMoney() {
//        when(this.wallet.getBalance()).thenReturn(new Money(70, Currency.INR));
//        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));
//        Money amount = new Money(63, Currency.INR);
//
//        assertDoesNotThrow(()->this.walletService.withdraw(TestConstants.USERNAME, TestConstants.WALLET_ID, amount));
//        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
//        verify(wallet, times(1)).withdraw(amount);
//        verify(wallet, never()).deposit(amount);
//        verify(this.walletRepository).save(any(Wallet.class));
//    }
//    @Test
//    public void test_shouldThrowInsufficientFundsExceptionOnWithdrawingExcessAmount(){
//        User dummyUser = new User(TestConstants.USER_ID, TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
//        Wallet dummyWallet = spy(new Wallet(dummyUser));
//
//        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(dummyUser));
//        when(this.walletRepository.findById(TestConstants.WALLET_ID)).thenReturn(Optional.of(dummyWallet));
//
//        assertThrows(InsufficientFunds.class, ()->this.walletService.withdraw(TestConstants.USERNAME, TestConstants.WALLET_ID, new Money(10, Currency.INR)));
//    }

//    @Test
//    public void test_shouldThrowUnauthorizedWalletActionExceptionIfTheGivenIdDoesNotMatchUserWalletIdWhileWithdrawing(){
//        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));
//        when(this.wallet.getId()).thenReturn(TestConstants.WALLET_ID);
//
//        assertThrows(UnauthorizedWalletAction.class, ()->this.walletService.withdraw(TestConstants.USERNAME, TestConstants.WALLET_ID+1, new Money()));
//    }
}
