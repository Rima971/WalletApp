package com.bank.walletapp.controllers;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.MoneyResponseDto;
import com.bank.walletapp.dtos.TransactRequestDto;
import com.bank.walletapp.entities.*;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.WalletNotFound;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void test_shouldAddWallet() throws Exception{
        when(this.walletService.addWallet(TestConstants.USERNAME)).thenReturn(new Wallet(this.testUser));

        mockMvc.perform(post(BASE_URL).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value(Message.WALLET_SUCCESSFULLY_CREATED.description))
                .andExpect(jsonPath("$.data.id").value(TestConstants.WALLET_ID))
                .andExpect(jsonPath("$.data.balance.amount").value(0.0))
                .andExpect(jsonPath("$.data.balance.currency").value(Currency.INR.name()));

        verify(this.walletService, times(1)).addWallet(TestConstants.USERNAME);
        verify(this.walletService, never()).deposit(anyString(), anyInt(), any(Money.class));
        verify(this.walletService, never()).withdraw(anyString(), anyInt(), any(Money.class));
        verify(this.walletService, never()).fetchWalletsByUsername(anyString());
    }
    @Test
    void test_shouldDepositMoney() throws Exception {
        Money money = new Money(50, Currency.INR);
        String mappedMoney = objectMapper.writeValueAsString(money);
        Wallet returnWallet = new Wallet(TestConstants.WALLET_ID, this.testUser, money);
        when(this.walletService.deposit(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenReturn(returnWallet);

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
        verify(this.walletService, never()).withdraw(anyString(), anyInt(), any(Money.class));
        verify(this.walletService, never()).fetchWalletsByUsername(anyString());
        verify(this.walletService, never()).addWallet(anyString());
    }

    @Test
    void test_shouldWithdrawMoney() throws Exception {
        Money money = new Money(30, Currency.INR);
        String mappedMoney = objectMapper.writeValueAsString(new Money());
        Wallet returnWallet = new Wallet(TestConstants.WALLET_ID, this.testUser, money);
        when(this.walletService.withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenReturn(returnWallet);

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(money.getNumericalValue()))
                .andExpect(jsonPath("$.data.currency").value(money.getCurrency().name()))
                .andExpect(jsonPath("$.message").value(Message.WALLET_SUCCESSFUL_WITHDRAWAL.description));

        verify(this.walletService, times(1)).withdraw(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class));
        verify(this.walletService, never()).deposit(anyString(), anyInt(), any(Money.class));
        verify(this.walletService, never()).addWallet(anyString());
        verify(this.walletService, never()).fetchWalletsByUsername(anyString());
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
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.BAD_REQUEST.value()))
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
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
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
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.UNAUTHORIZED.value()))
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
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CONFLICT.value()))
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
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));
    }

    @Test
    void test_shouldFetchAllWalletsForAUser() throws Exception {
        List<Wallet> wallets = new ArrayList<>();
        for (int i=0; i<5; i++){
            wallets.add(new Wallet(this.testUser));
        }
        when(this.walletService.fetchWalletsByUsername(TestConstants.USERNAME)).thenReturn(wallets);
        String mappedWallets = objectMapper.writeValueAsString(wallets.stream().map(wallet->new MoneyResponseDto(wallet.getBalance())).toList());

        mockMvc.perform(get(BASE_URL)
                        .with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD))
                )
                .andExpect(status().isOk());
    }
    @Test
    void test_shouldTransactMoneyBetweenUsers() throws Exception {
        Wallet senderWallet = new Wallet(this.testUser);
        senderWallet.deposit(new Money(10, Currency.INR));
        User receiver = new User(TestConstants.USER_ID+1, TestConstants.TRANSACTION_RECEIVER_USERNAME, TestConstants.PASSWORD, Country.INDIA);
        Wallet receiverWallet = new Wallet(receiver);
        Money amountToTransact = new Money(30, Currency.INR);
        TransactRequestDto transactionRequest = new TransactRequestDto(amountToTransact.getNumericalValue(), amountToTransact.getCurrency(), receiverWallet.getId());
        String mappedTransactionRequest = objectMapper.writeValueAsString(transactionRequest);
        TransactionRecord record = new TransactionRecord(senderWallet, receiverWallet, amountToTransact);
        when(this.walletService.transact(eq(TestConstants.WALLET_ID), eq(TestConstants.USERNAME), eq(transactionRequest))).thenReturn(record);

        mockMvc.perform(put(BASE_URL + "/" + TestConstants.WALLET_ID + "/transact").contentType(MediaType.APPLICATION_JSON).content(mappedTransactionRequest).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value(Message.WALLETS_SUCCESSFUL_TRANSACTION.description))
                .andExpect(jsonPath("$.data.sender").value(TestConstants.USERNAME))
                .andExpect(jsonPath("$.data.receiver").value(TestConstants.TRANSACTION_RECEIVER_USERNAME))
                .andExpect(jsonPath("$.data.amount.numericalValue").value(amountToTransact.getNumericalValue()))
                .andExpect(jsonPath("$.data.amount.currency").value(amountToTransact.getCurrency().name()))
                .andExpect(jsonPath("$.data.timestamp").value(IsNull.nullValue()));

        verify(this.walletService, times(1)).transact(eq(TestConstants.WALLET_ID), eq(TestConstants.USERNAME), eq(transactionRequest));
        verify(this.walletService, never()).deposit(anyString(), anyInt(), any(Money.class));
        verify(this.walletService, never()).withdraw(anyString(), anyInt(), any(Money.class));
        verify(this.walletService, never()).fetchWalletsByUsername(anyString());
        verify(this.walletService, never()).addWallet(anyString());
    }

}
