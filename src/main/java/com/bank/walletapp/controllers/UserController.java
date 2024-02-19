package com.bank.walletapp.controllers;

import com.bank.walletapp.dtos.RegisterDto;
import com.bank.walletapp.exceptions.UsernameAlreadyExists;
import com.bank.walletapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto){
        try{
            this.userService.register(registerDto.getUsername(), registerDto.getPassword());
            return new ResponseEntity<>("User is registered successfully", HttpStatus.OK);
        } catch (UsernameAlreadyExists e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
