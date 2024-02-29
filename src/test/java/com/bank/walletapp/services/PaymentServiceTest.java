package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.dtos.TransactRequestDto;
import com.bank.walletapp.entities.*;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.exceptions.InvalidTransactionReceiver;
import com.bank.walletapp.repositories.PaymentRepository;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private Payment payment;

    @InjectMocks
    private PaymentService paymentService;
    private User mockUser = spy(new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA));

    private Wallet wallet = new Wallet(this.mockUser);
    @BeforeEach
    void setup(){
        openMocks(this);
    }
    @Test
    public void test_shouldTransactInSameCurrency(){
        TransactRequestDto request = new TransactRequestDto();
        request.setWalletId(TestConstants.WALLET_ID+1);
        request.setAmount(10);
        request.setCurrency(Currency.INR.name());
        assertDoesNotThrow(()->this.paymentService.create(TestConstants.WALLET_ID, TestConstants.TRANSACTION_SENDER_USERNAME, request));
        verify(this.paymentRepository, times(1)).save(this.payment);
    }
    @Test
    public void test_shouldFetchListOfTransactionsForAnAuthenticatedUserByUsername(){
        Payment dummyRecord = new Payment(new Wallet(), new Wallet(), new Money());
        List<Payment> log = new ArrayList<>(List.of(dummyRecord, dummyRecord));
        when(this.paymentRepository.findBySenderUserId(TestConstants.USER_ID)).thenReturn(log);
        when(this.paymentRepository.findByReceiverUserId(TestConstants.USER_ID)).thenReturn(log);

        assertDoesNotThrow(()-> {
            List<Payment> result = this.paymentService.fetchAll(TestConstants.USERNAME);
            assertTrue(result.stream().allMatch(r->r==dummyRecord));
            assertEquals(log.size()*2, result.size());
        });
        verify(this.paymentRepository).findBySenderUserId(TestConstants.USER_ID);
        verify(this.paymentRepository).findByReceiverUserId(TestConstants.USER_ID);
    }

    @Test
    public void test_shouldThrowInvalidTransactionReceiverWhenAttemptingToTransactingMoneyToTheSameWallet(){
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(this.mockUser));
        TransactRequestDto request = new TransactRequestDto();
        request.setWalletId(TestConstants.WALLET_ID);
        request.setCurrency(Currency.INR.name());
        request.setWalletId(TestConstants.WALLET_ID);

        assertThrows(InvalidTransactionReceiver.class, ()->this.paymentService.create(TestConstants.WALLET_ID, TestConstants.USERNAME, request));
    }


}
