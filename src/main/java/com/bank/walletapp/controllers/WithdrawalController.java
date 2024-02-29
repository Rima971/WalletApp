package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.MoneyRequestDto;
import com.bank.walletapp.dtos.MoneyResponseDto;
import com.bank.walletapp.entities.Deposit;
import com.bank.walletapp.entities.GenericHttpResponse;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Withdrawal;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.services.WithdrawalService;
import com.bank.walletapp.utils.ExceptionUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/wallets/{walletId}/withdrawals")
public class WithdrawalController {
    @Autowired
    private WithdrawalService withdrawalService;

    @PostMapping("")
    public ResponseEntity<GenericHttpResponse> create(Authentication authentication, @PathVariable int walletId, @Valid @RequestBody MoneyRequestDto amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            Withdrawal withdrawal = this.withdrawalService.create(userDetails.getUsername(), walletId, amount.getMoney());
            Money balance = withdrawal.getWallet().getBalance();
            return GenericHttpResponse.create(HttpStatus.OK, Message.WALLET_SUCCESSFUL_WITHDRAWAL.description, new MoneyResponseDto(balance));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }
    }
}
