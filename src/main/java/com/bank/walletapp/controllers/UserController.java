package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.GenericResponseDto;
import com.bank.walletapp.dtos.RegisterRequestDto;
import com.bank.walletapp.dtos.UserResponseDto;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.exceptions.UserNotFound;
import com.bank.walletapp.exceptions.UsernameAlreadyExists;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.services.UserService;
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
    public ResponseEntity<GenericResponseDto> registerUser(@RequestBody RegisterRequestDto registerRequestDto){
        try{
            User savedUser = this.userService.register(registerRequestDto.getUsername(), registerRequestDto.getPassword());
            return GenericResponseDto.create(HttpStatus.CREATED, Message.USER_SUCCESSFUL_REGISTRATION.description, new UserResponseDto(savedUser));
        } catch (UsernameAlreadyExists e){
            return GenericResponseDto.create(HttpStatus.CONFLICT, Message.USER_ALREADY_EXISTS.description, null);
        } catch (WalletNotFound e) {
            return GenericResponseDto.create(HttpStatus.CONFLICT, Message.WALLET_NOT_FOUND.description, null);
        } catch (Exception e) {
            return GenericResponseDto.create(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

    @PatchMapping("")
    public ResponseEntity<GenericResponseDto> deleteUser(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try {
            this.userService.deleteUserByUsername(userDetails.getUsername());
            return GenericResponseDto.create(HttpStatus.OK, Message.USER_SUCCESSFUL_DELETION.description, null);
        } catch (UserNotFound e){
            return GenericResponseDto.create(HttpStatus.CONFLICT, e.getMessage(), null);
        } catch (WalletNotFound e){
            return GenericResponseDto.create(HttpStatus.CONFLICT, Message.WALLET_NOT_FOUND.description, null);
        } catch (Exception e) {
            return GenericResponseDto.create(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }

    }

}
