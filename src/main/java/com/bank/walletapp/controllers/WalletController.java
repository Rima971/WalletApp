package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.*;
import com.bank.walletapp.entities.TransactionRecord;
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
@RequestMapping("/api/v1/wallets")
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
    public ResponseEntity<GenericResponseDto> addWallet(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        try{
            Wallet savedWallet = this.walletService.addWallet(userDetails.getUsername());
            return GenericResponseDto.create(HttpStatus.CREATED, Message.WALLET_SUCCESSFULLY_CREATED.description, new WalletResponseDto(savedWallet));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }

    }

    @PutMapping("/{walletId}/deposit")
    public ResponseEntity<GenericResponseDto> deposit(Authentication authentication, @PathVariable int walletId, @Valid @RequestBody MoneyRequestDto amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            Wallet updatedWallet = this.walletService.deposit(userDetails.getUsername(), walletId, amount.getMoney());
            Money balance = updatedWallet.getBalance();
            return GenericResponseDto.create(HttpStatus.OK, Message.WALLET_SUCCESSFUL_DEPOSIT.description, new MoneyResponseDto(balance));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }
    }

    @PutMapping("/{walletId}/withdraw")
    public ResponseEntity<GenericResponseDto> withdraw(Authentication authentication, @PathVariable int walletId, @RequestBody Money amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            Wallet updatedWallet = this.walletService.withdraw(userDetails.getUsername(), walletId, amount);
            Money balance = updatedWallet.getBalance();
            return GenericResponseDto.create(HttpStatus.OK, Message.WALLET_SUCCESSFUL_WITHDRAWAL.description, new MoneyResponseDto(balance));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }

    }

    @PutMapping("/{walletId}/transact")
    public ResponseEntity<GenericResponseDto> transact(Authentication authentication, @PathVariable int walletId, @Valid @RequestBody TransactRequestDto transactRequestDto){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            TransactionRecord record = this.walletService.transact(walletId, userDetails.getUsername(), transactRequestDto);
            return GenericResponseDto.create(HttpStatus.OK, Message.WALLETS_SUCCESSFUL_TRANSACTION.description, new TransactionRecordResponseDto(record));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }
    }
}
