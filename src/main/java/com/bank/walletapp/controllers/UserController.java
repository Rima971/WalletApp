package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.entities.GenericHttpResponse;
import com.bank.walletapp.dtos.RegisterRequestDto;
import com.bank.walletapp.dtos.UserResponseDto;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.utils.ExceptionUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<GenericHttpResponse> create(@Valid @RequestBody RegisterRequestDto registerRequestDto){
        try{
            User savedUser = this.userService.register(registerRequestDto.getUsername(), registerRequestDto.getPassword(), registerRequestDto.getCountry());
            return GenericHttpResponse.create(HttpStatus.CREATED, Message.USER_SUCCESSFUL_REGISTRATION.description, new UserResponseDto(savedUser));
        } catch (Exception e) {
            return ExceptionUtils.handle(e);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<GenericHttpResponse> delete(Authentication authentication, @PathVariable int userId){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try {
            this.userService.deleteUserByUsername(userDetails.getUsername());
            return GenericHttpResponse.create(HttpStatus.OK, Message.USER_SUCCESSFUL_DELETION.description, null);
        } catch (Exception e) {
            return ExceptionUtils.handle(e);
        }

    }

    @GetMapping("")
    public ResponseEntity<GenericHttpResponse> fetch(Authentication authentication, @PathVariable int userId){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try {
            User fetchedUser = this.userService.fetchUserByUsername(userDetails.getUsername());
            return GenericHttpResponse.create(HttpStatus.OK, Message.USER_FOUND.description, new UserResponseDto(fetchedUser));
        } catch (Exception e) {
            return ExceptionUtils.handle(e);
        }

    }

}
