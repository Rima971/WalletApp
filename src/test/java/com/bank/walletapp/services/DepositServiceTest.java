package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DepositServiceTest {
//    @Test
//    public void test_shouldBeAbleToDepositMoney() {
//        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));
//        Money amount = new Money(63, Currency.INR);
//
//        assertDoesNotThrow(()->this.walletService.deposit(TestConstants.USERNAME, TestConstants.WALLET_ID, amount));
//        verify(this.userRepository, times(1)).findByUsername(TestConstants.USERNAME);
//        verify(this.wallet, times(1)).deposit(amount);
//        verify(this.wallet, never()).withdraw(amount);
//        verify(this.walletRepository, times(1)).save(this.wallet);
//    }

//    @Test
//    public void test_shouldThrowUnauthorizedWalletActionExceptionIfTheGivenIdDoesNotMatchUserWalletIdWhileDepositing(){
//        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));
//        when(this.wallet.getId()).thenReturn(TestConstants.WALLET_ID);
//
//        assertThrows(UnauthorizedWalletAction.class, ()->this.walletService.deposit(TestConstants.USERNAME, TestConstants.WALLET_ID+1, new Money()));
//    }

}
