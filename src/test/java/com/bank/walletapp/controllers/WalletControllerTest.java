package com.bank.walletapp.controllers;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.BalanceResponseDto;
import com.bank.walletapp.dtos.TransactRequestDto;
import com.bank.walletapp.entities.*;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {
    private static final String BASE_URL = "/api/v1/wallets";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @MockBean
    private UserService userService;

    private final User testUser = new User(TestConstants.USERNAME, new BCryptPasswordEncoder().encode(TestConstants.PASSWORD), Country.INDIA);

    @BeforeEach
    void setUp() {
        reset(this.walletService);
        reset(this.userService);
        when(this.userService.loadUserByUsername(TestConstants.USERNAME)).thenReturn(new CustomUserDetails(this.testUser));
    }
    @Test
    void test_shouldDepositMoney() throws Exception {
        Money money = new Money(50, Currency.INR);
        String mappedMoney = objectMapper.writeValueAsString(money);
        when(this.walletService.deposit(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenReturn(new Wallet(TestConstants.WALLET_ID, money));

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney)
                                .with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD))
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(money.getNumericalValue()))
                .andExpect(jsonPath("$.data.currency").value(money.getCurrency().name()))
                .andExpect(jsonPath("$.message").value(Message.WALLET_SUCCESSFUL_DEPOSIT.description));

        verify(this.walletService, times(1)).deposit(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class));
        verify(this.walletService, never()).createWallet();
        verify(this.walletService, never()).withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class));
        verify(this.walletService, never()).fetchAllWallets();
    }

    @Test
    void test_shouldWithdrawMoney() throws Exception {
        Money money = new Money(30, Currency.INR);
        String mappedMoney = objectMapper.writeValueAsString(money);
        when(this.walletService.withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenReturn(new Wallet(TestConstants.WALLET_ID, money));

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(money.getNumericalValue()))
                .andExpect(jsonPath("$.data.currency").value(money.getCurrency().name()))
                .andExpect(jsonPath("$.message").value(Message.WALLET_SUCCESSFUL_WITHDRAWAL.description));

        verify(this.walletService, times(1)).withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class));
        verify(this.walletService, never()).createWallet();
        verify(this.walletService, never()).deposit(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class));
        verify(this.walletService, never()).fetchAllWallets();
    }

    @Test
    void test_shouldThrow401UnauthorizedException_WhenDepositingMoneyWithoutBasicAuth() throws Exception {
        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/deposit"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test_shouldThrow401UnauthorizedException_WhenWithdrawingMoneyWithoutBasicAuth() throws Exception {
        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/withdraw"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test_shouldThrow400BadRequest_WhenInsufficientFundsExceptionIsThrownWhileWithdrawing() throws Exception {
        when(this.walletService.withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenThrow(InsufficientFunds.class);
        String mappedMoney = objectMapper.writeValueAsString(new Money());

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Message.WALLET_INSUFFICIENT_FUNDS.description))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
    }

    @Test
    void test_shouldThrow409Conflict_WhenWalletNotFoundExceptionIsThrownWhileWithdrawing() throws Exception {
        when(this.walletService.withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenThrow(WalletNotFound.class);
        String mappedMoney = objectMapper.writeValueAsString(new Money());

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(Message.WALLET_NOT_FOUND.description))
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
    }

    @Test
    void test_shouldThrow401Unauthorized_WhenUnauthorizedWalletActionExceptionIsThrownWhileWithdrawing() throws Exception {
        when(this.walletService.withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenThrow(UnauthorizedWalletAction.class);
        String mappedMoney = objectMapper.writeValueAsString(new Money());

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(Message.WALLET_UNAUTHORIZED_USER_ACTION.description))
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
    }

    @Test
    void test_shouldThrow409Conflict_WhenWalletNotFoundExceptionIsThrownWhileDepositing() throws Exception {
        when(this.walletService.deposit(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenThrow(WalletNotFound.class);
        String mappedMoney = objectMapper.writeValueAsString(new Money());

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(Message.WALLET_NOT_FOUND.description))
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
    }

    @Test
    void test_shouldThrow401Unauthorized_WhenUnauthorizedWalletActionExceptionIsThrownWhileDepositing() throws Exception {
        when(this.walletService.deposit(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenThrow(UnauthorizedWalletAction.class);
        String mappedMoney = objectMapper.writeValueAsString(new Money());

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney)
                        .with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(Message.WALLET_UNAUTHORIZED_USER_ACTION.description))
                .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
    }

    @Test
    void test_shouldFetchAllWallets() throws Exception {
        List<Wallet> wallets = new ArrayList<>();
        for (int i=0; i<5; i++){
            wallets.add(new Wallet(i, new Money()));
        }
        when(this.walletService.fetchAllWallets()).thenReturn(wallets);
        String mappedWallets = objectMapper.writeValueAsString(wallets.stream().map(wallet->new BalanceResponseDto(wallet.getBalance())).toList());

        mockMvc.perform(get(BASE_URL)
                        .with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD))
                )
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.*", isA(ArrayList.class)));
    }
    @Test
    void test_shouldTransactMoneyBetweenUsers() throws Exception {
        this.testUser.getWallet().deposit(new Money(10, Currency.INR));
        User receiver = new User(TestConstants.USER_ID+1, TestConstants.TRANSACTION_RECEIVER_USERNAME, TestConstants.PASSWORD, Country.INDIA, new Wallet());
        Money amountToTransact = new Money(30, Currency.INR);
        TransactRequestDto transactionRequest = new TransactRequestDto(amountToTransact.getNumericalValue(), amountToTransact.getCurrency(), receiver.getUsername());
        String mappedTransactionRequest = objectMapper.writeValueAsString(transactionRequest);
        TransactionRecord record = new TransactionRecord(this.testUser, receiver, amountToTransact);
        when(this.walletService.transact(eq(TestConstants.WALLET_ID), eq(TestConstants.USERNAME), eq(TestConstants.TRANSACTION_RECEIVER_USERNAME), any(Money.class))).thenReturn(record);

        mockMvc.perform(put(BASE_URL + "/" + TestConstants.WALLET_ID + "/transact").contentType(MediaType.APPLICATION_JSON).content(mappedTransactionRequest).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(Message.WALLETS_SUCCESSFUL_TRANSACTION.description))
                .andExpect(jsonPath("$.data.sender").value(TestConstants.USERNAME))
                .andExpect(jsonPath("$.data.receiver").value(TestConstants.TRANSACTION_RECEIVER_USERNAME))
                .andExpect(jsonPath("$.data.amount.numericalValue").value(amountToTransact.getNumericalValue()))
                .andExpect(jsonPath("$.data.amount.currency").value(amountToTransact.getCurrency().name()))
                .andExpect(jsonPath("$.data.timestamp").value(IsNull.nullValue()));

        verify(this.walletService, times(1)).transact(eq(TestConstants.WALLET_ID), eq(TestConstants.USERNAME), eq(TestConstants.TRANSACTION_RECEIVER_USERNAME), any(Money.class));
    }

}
