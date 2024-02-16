package com.bank.walletapp.controllers;

import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.models.Money;
import com.bank.walletapp.models.Wallet;
import com.bank.walletapp.services.WalletService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @GetMapping("/{walletId}")
    public ResponseEntity<Money> getBalanceFromId(@PathVariable int walletId) throws WalletNotFound {
        try{
            Money balance = this.walletService.getBalanceFromId(walletId);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        } catch (WalletNotFound e){
            return ResponseEntity.badRequest().body(null);
        }

    }

    @GetMapping("/")
    public List<Wallet> fetchAllWallets(){
        return this.walletService.fetchAllWallets();
    }

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(){
        Wallet returnedWallet = this.walletService.createWallet();
        System.out.println(returnedWallet);
        return new ResponseEntity<>(returnedWallet, HttpStatus.CREATED);
    }

    @PatchMapping("/{walletId}/deposit")
    public String deposit(@PathVariable int walletId, @RequestBody Money amount) throws WalletNotFound {
        this.walletService.deposit(walletId, amount);
        return amount + " amount deposited successfully in wallet with id " + walletId;
    }

    @PatchMapping("/{walletId}/withdraw")
    public String withdraw(@PathVariable int walletId, @RequestBody Money amount) throws WalletNotFound {
        this.walletService.withdraw(walletId, amount);
        return amount + " amount withdrawed successfully from wallet with id "+walletId;
    }

    @DeleteMapping("/{walletId}")
    public String delete(@PathVariable int walletId) throws WalletNotFound {
        this.walletService.deleteWallet(walletId);
        return "Wallet successfully deleted";
    }
}
