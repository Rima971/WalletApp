package com.bank.walletapp;

import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private User user = new User("test", "test", new Wallet());

    @InjectMocks
    private UserService userService;


    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    public void test_shouldRegisterUser(){
        assertDoesNotThrow(()->this.userService.register("test", "test"));
        verify(this.userRepository, times(1)).existsByUsername("test");
        verify(this.userRepository, times(1)).save(any(User.class));
        verify(this.walletService, times(1)).createWallet();
    }


}
