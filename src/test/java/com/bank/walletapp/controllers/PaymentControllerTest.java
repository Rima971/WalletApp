package com.bank.walletapp.controllers;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.TransactRequestDto;
import com.bank.walletapp.entities.*;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.services.PaymentService;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {
    private static final String BASE_URL = "/api/v1/wallets/"+TestConstants.WALLET_ID+"/transactions";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private PaymentService paymentService;

    private final User testUser = new User(TestConstants.USERNAME, new BCryptPasswordEncoder().encode(TestConstants.PASSWORD), Country.INDIA);

    @BeforeEach
    void setUp() {
        reset(this.paymentService);
        reset(this.userService);
        when(this.userService.loadUserByUsername(TestConstants.USERNAME)).thenReturn(new CustomUserDetails(this.testUser));
    }
    @Test
    void test_shouldTransactMoneyBetweenUsers() throws Exception {
        Wallet senderWallet = new Wallet(this.testUser);
        senderWallet.deposit(new Money(10, Currency.INR));
        User receiver = new User(TestConstants.USER_ID+1, TestConstants.TRANSACTION_RECEIVER_USERNAME, TestConstants.PASSWORD, Country.INDIA);
        Wallet receiverWallet = new Wallet(receiver);
        Money amountToTransact = new Money(30, Currency.INR);
        TransactRequestDto transactionRequest = new TransactRequestDto();
        transactionRequest.setWalletId(receiverWallet.getId());
        transactionRequest.setCurrency(amountToTransact.getCurrency().name());
        transactionRequest.setAmount(amountToTransact.getNumericalValue());
        String mappedTransactionRequest = objectMapper.writeValueAsString(transactionRequest);
        Payment record = new Payment(senderWallet, receiverWallet, amountToTransact);
        when(this.paymentService.create(eq(TestConstants.WALLET_ID), eq(TestConstants.USERNAME), eq(transactionRequest))).thenReturn(record);

        mockMvc.perform(put(BASE_URL + "/" + TestConstants.WALLET_ID + "/transact").contentType(MediaType.APPLICATION_JSON).content(mappedTransactionRequest).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(Message.TRANSACTION_SUCCESSFUL.description))
                .andExpect(jsonPath("$.data.sender").value(TestConstants.USERNAME))
                .andExpect(jsonPath("$.data.receiver").value(TestConstants.TRANSACTION_RECEIVER_USERNAME))
                .andExpect(jsonPath("$.data.amount.numericalValue").value(amountToTransact.getNumericalValue()))
                .andExpect(jsonPath("$.data.amount.currency").value(amountToTransact.getCurrency().name()))
                .andExpect(jsonPath("$.data.timestamp").value(IsNull.nullValue()));

        verify(this.paymentService, times(1)).create(eq(TestConstants.WALLET_ID), eq(TestConstants.USERNAME), eq(transactionRequest));
//        verify(this.walletService, never()).deposit(anyString(), anyInt(), any(Money.class));
//        verify(this.walletService, never()).withdraw(anyString(), anyInt(), any(Money.class));
//        verify(this.walletService, never()).fetchWalletsByUsername(anyString());
//        verify(this.walletService, never()).create(anyString());
    }
}
