package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.MoneyRequestDto;
import com.bank.walletapp.dtos.MoneyResponseDto;
import com.bank.walletapp.entities.Deposit;
import com.bank.walletapp.entities.GenericHttpResponse;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.services.DepositService;
import com.bank.walletapp.utils.ExceptionUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/wallets/{walletId}/deposits")
public class DepositController {
    @Autowired
    private DepositService depositService;

    @PostMapping("")
    public ResponseEntity<GenericHttpResponse> create(Authentication authentication, @PathVariable int walletId, @Valid @RequestBody MoneyRequestDto amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            Deposit deposit = this.depositService.create(userDetails.getUsername(), walletId, amount.getMoney());
            Money balance = deposit.getWallet().getBalance();
            return GenericHttpResponse.create(HttpStatus.OK, Message.WALLET_SUCCESSFUL_DEPOSIT.description, new MoneyResponseDto(balance));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }
    }
}
