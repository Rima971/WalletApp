package com.bank.walletapp;

import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.models.Money;
import com.bank.walletapp.models.Wallet;
import com.bank.walletapp.services.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        reset(this.walletService);
    }


    @Test
    void test_createWalletShouldReturnSuccess() throws Exception {
        when(this.walletService.createWallet()).thenReturn(new Wallet(0, new Money(), false));

        this.mockMvc.perform(post("/wallet/create").with(httpBasic("user", "password")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.balance.numericalValue").value(0))
                .andExpect(jsonPath("$.balance.currency").value("INR"))
                .andExpect(jsonPath("$.id").value(0));

        verify(this.walletService, times(1)).createWallet();
        verify(this.walletService, never()).deposit(any(Integer.class), any(Money.class));
        verify(this.walletService, never()).withdraw(any(Integer.class), any(Money.class));
        verify(this.walletService, never()).fetchAllWallets();
    }

    @Test
    void test_shouldDepositMoney() throws Exception {
        Money money = new Money(50, Currency.INR);
        String mappedMoney = objectMapper.writeValueAsString(money);

        mockMvc.perform(patch("/wallet/1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney)
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk());

        verify(this.walletService, times(1)).deposit(eq(1), any(Money.class));
        verify(this.walletService, never()).createWallet();
        verify(this.walletService, never()).withdraw(any(Integer.class), any(Money.class));
        verify(this.walletService, never()).fetchAllWallets();
    }

    @Test
    void test_shouldWithdrawMoney() throws Exception {
        Money moneyToWithdraw = new Money(30, Currency.INR);
        String mappedMoney = objectMapper.writeValueAsString(moneyToWithdraw);

        mockMvc.perform(patch("/wallet/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappedMoney).with(httpBasic("user", "password")))
                .andExpect(status().isOk());

        verify(this.walletService, times(1)).withdraw(eq(1), any(Money.class));
        verify(this.walletService, never()).createWallet();
        verify(this.walletService, never()).deposit(any(Integer.class), any(Money.class));
        verify(this.walletService, never()).fetchAllWallets();
    }

    @Test
    void test_shouldThrow401UnauthorizedExceptionWhenDepositingMoneyWithoutBasicAuth() throws Exception {
        mockMvc.perform(patch("/wallet/1/deposit"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test_shouldThrow401UnauthorizedExceptionWhenWithdrawingMoneyWithoutBasicAuth() throws Exception {
        mockMvc.perform(patch("/wallet/1/withdraw"))
                .andExpect(status().isUnauthorized());
    }

}
