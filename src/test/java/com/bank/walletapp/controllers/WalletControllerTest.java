package com.bank.walletapp.controllers;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.MoneyResponseDto;
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
        when(this.walletService.create(TestConstants.USERNAME)).thenReturn(new Wallet(this.testUser));

        mockMvc.perform(post(BASE_URL).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value(Message.WALLET_SUCCESSFULLY_CREATED.description))
                .andExpect(jsonPath("$.data.id").value(TestConstants.WALLET_ID))
                .andExpect(jsonPath("$.data.balance.amount").value(0.0))
                .andExpect(jsonPath("$.data.balance.currency").value(Currency.INR.name()));

        verify(this.walletService, times(1)).create(TestConstants.USERNAME);
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

}
