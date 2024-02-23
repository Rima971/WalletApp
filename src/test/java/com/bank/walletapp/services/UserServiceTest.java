package com.bank.walletapp.services;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.entities.Country;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.exceptions.UserNotFound;
import com.bank.walletapp.exceptions.UsernameAlreadyExists;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        assertDoesNotThrow(()->this.userService.register(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA));
        verify(this.userRepository, times(1)).existsByUsername(TestConstants.USERNAME);
        verify(this.userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void test_shouldThrowUserNameAlreadyExistsIfARepeatedUsernameIsPassedDuringRegistration(){
        User existingUser = new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(existingUser));
        when(this.userRepository.existsByUsername(TestConstants.USERNAME)).thenReturn(true);

        assertThrows(UsernameAlreadyExists.class, ()->this.userService.register(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA));
    }

    @Test
    public void test_shouldDeleteUserIfExists(){
        User existingUser = new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(()->this.userService.deleteUserByUsername(TestConstants.USERNAME));
    }

    @Test
    public void test_shouldThrowUserDoesNotExistWhenAttemptingToDeleteAUserThatDoesNotExist(){
        when(this.userRepository.findByUsername(TestConstants.USERNAME)).thenThrow(UserNotFound.class);

        assertThrows(UserNotFound.class, ()->this.userService.deleteUserByUsername(TestConstants.USERNAME));
    }

}
