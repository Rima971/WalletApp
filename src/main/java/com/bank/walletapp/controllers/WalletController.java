package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.exceptions.InsuffiucientFunds;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.dtos.TransactRequestDto;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
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

    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public List<Wallet> fetchAllWallets(){
        return this.walletService.fetchAllWallets();
    }

    @PatchMapping("/deposit")
    public ResponseEntity<String> deposit(Authentication authentication, @RequestBody Money amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            this.walletService.deposit(userDetails.getUsername(), amount);
            return ResponseEntity.ok(amount.getNumericalValue()+" "+amount.getCurrency() + " amount deposited successfully in wallet");
        } catch (WalletNotFound e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/withdraw")
    public ResponseEntity<String> withdraw(Authentication authentication, @RequestBody Money amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            this.walletService.withdraw(userDetails.getUsername(), amount);
            return ResponseEntity.ok(amount.getNumericalValue()+" "+amount.getCurrency() + " amount withdrawed successfully from wallet");
        } catch (InsuffiucientFunds e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (WalletNotFound e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @PatchMapping("/transact")
    public ResponseEntity<String> transact(Authentication authentication, @RequestBody TransactRequestDto transactRequestDto){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            this.walletService.transact(userDetails.getUsername(), transactRequestDto.getUsername(), transactRequestDto.getMoney());
            return ResponseEntity.ok("Transaction occurred successfully");
        } catch (Exception e){
             return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
