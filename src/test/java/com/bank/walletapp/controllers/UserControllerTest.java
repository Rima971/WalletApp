package com.bank.walletapp.controllers;

import com.bank.walletapp.TestConstants;
import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.entities.Country;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.exceptions.UsernameAlreadyExists;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    private static final String BASE_URL = "/api/v1/users";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        reset(this.userService);
    }

    @Test
    public void test_shouldRegisterUser_created() throws Exception {
        User user = new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
        String mappedUser = objectMapper.writeValueAsString(user);
        when(this.userService.register(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA)).thenReturn(user);
        this.mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(mappedUser))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value(Message.USER_SUCCESSFUL_REGISTRATION.description))
                .andExpect(jsonPath("$.data.username").value(TestConstants.USERNAME))
                .andExpect(jsonPath("$.data.country").value(Country.INDIA.name()));
        verify(this.userService, times(1)).register(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
    }

    @Test
    public void test_shouldThrow409Conflict_WhenRegisteringWithAUsernameThatAlreadyExists() throws Exception {
        User user = new User(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA);
        String mappedUser = objectMapper.writeValueAsString(user);
        when(this.userService.register(TestConstants.USERNAME, TestConstants.PASSWORD, Country.INDIA)).thenThrow(UsernameAlreadyExists.class);
        this.mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(mappedUser))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value(Message.USER_ALREADY_EXISTS.description))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));

    }

    @Test
    public void test_shouldDeleteUserIfExists() throws Exception {
        User testUser = new User(TestConstants.USERNAME, new BCryptPasswordEncoder().encode(TestConstants.PASSWORD), Country.INDIA);
        when(this.userService.loadUserByUsername(TestConstants.USERNAME)).thenReturn(new CustomUserDetails(testUser));

        this.mockMvc.perform(delete(BASE_URL+"/"+TestConstants.USER_ID).with(httpBasic(TestConstants.USERNAME, TestConstants.PASSWORD)))
                .andExpect(status().isOk());

        verify(this.userService, times(1)).deleteUserByUsername(TestConstants.USERNAME);
    }
}
