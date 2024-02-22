package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    public void test_shouldRegisterUser(){
        assertDoesNotThrow(()->this.userService.register(TestConstants.USERNAME, TestConstants.PASSWORD));
        verify(this.userRepository, times(1)).existsByUsername(TestConstants.USERNAME);
        verify(this.userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void test_shouldDeleteUser(){

    }

}
