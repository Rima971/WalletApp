package com.bank.walletapp.controllers;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.entities.*;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.services.DepositService;
import com.bank.walletapp.services.PaymentService;
import com.bank.walletapp.services.UserService;
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
public class DepositControllerTest {
    private static final String BASE_URL = "/api/v1/users/"+TestConstants.USER_ID+"/wallets/"+TestConstants.WALLET_ID+"/deposits";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepositService depositService;

    @MockBean
    private UserService userService;
    @MockBean
    private PaymentService paymentService;
    private final User testUser = new User(TestConstants.USERNAME, new BCryptPasswordEncoder().encode(TestConstants.PASSWORD), Country.INDIA);

    @BeforeEach
    void setUp() {
        reset(this.depositService);
        reset(this.userService);
        when(this.userService.loadUserByUsername(TestConstants.USERNAME)).thenReturn(new CustomUserDetails(this.testUser));
    }

    @Test
    void test_shouldDepositMoney() throws Exception {
        Money money = new Money(50, Currency.INR);
        String mappedMoney = objectMapper.writeValueAsString(money);
        Wallet returnWallet = new Wallet(TestConstants.WALLET_ID, this.testUser, money);
        when(this.depositService.create(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenReturn(new Deposit(returnWallet, money));

        mockMvc.perform(put(BASE_URL + "/"+TestConstants.WALLET_ID+"/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney)
                        .with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(money.getNumericalValue()))
                .andExpect(jsonPath("$.data.currency").value(money.getCurrency().name()))
                .andExpect(jsonPath("$.message").value(Message.WALLET_SUCCESSFUL_DEPOSIT.description));

        verify(this.depositService, times(1)).create(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class));
//        verify(this.depositService, never()).withdraw(anyString(), anyInt(), any(Money.class));
//        verify(this.depositService, never()).fetchWalletsByUsername(anyString());
//        verify(this.depositService, never()).create(anyString());
    }
    @Test
    void test_shouldThrow409Conflict_WhenWalletNotFoundExceptionIsThrownWhileDepositing() throws Exception {
        when(this.depositService.create(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenThrow(WalletNotFound.class);
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
        when(this.depositService.create(eq(TestConstants.USERNAME), eq(TestConstants.WALLET_ID), any(Money.class))).thenThrow(UnauthorizedWalletAction.class);
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

}
