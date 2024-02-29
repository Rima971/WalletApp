package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.*;
import com.bank.walletapp.entities.GenericHttpResponse;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.services.WalletService;
import com.bank.walletapp.utils.ExceptionUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @GetMapping("")
    public ResponseEntity<List<MoneyResponseDto>> fetchAllWallets(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        List<MoneyResponseDto> wallets = this.walletService.fetchWalletsByUsername(userDetails.getUsername()).stream().map(wallet->new MoneyResponseDto(wallet.getBalance())).toList();
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<GenericHttpResponse> createWallet(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        try{
            Wallet savedWallet = this.walletService.create(userDetails.getUsername());
            return GenericHttpResponse.create(HttpStatus.CREATED, Message.WALLET_SUCCESSFULLY_CREATED.description, new WalletResponseDto(savedWallet));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }

    }
}
