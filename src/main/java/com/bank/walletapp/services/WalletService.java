package com.bank.walletapp.services;

import com.bank.walletapp.exceptions.*;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    public Money getBalanceFromId(int walletId) throws WalletNotFound {
        Optional<Wallet> result = this.walletRepository.findById(walletId);
        return result.orElseThrow(WalletNotFound::new).getBalance();
    }

    public Wallet createWallet(){
        return this.walletRepository.save(new Wallet());
    }

    public void deposit(int id, Money amount) throws WalletNotFound, InvalidRequest {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getCredentials() +" "+ authentication.getName());
        Wallet wallet = this.walletRepository.findById(id).orElseThrow(WalletNotFound::new);
        wallet.deposit(amount);

        this.walletRepository.save(wallet);
    }

    public void withdraw(int id, Money amount) throws WalletNotFound, InvalidRequest, InsuffiucientFunds {
        Wallet wallet = this.walletRepository.findById(id).orElseThrow(WalletNotFound::new);
        wallet.withdraw(amount);
        this.walletRepository.save(wallet);
    }

    public void deleteWallet(int id) throws WalletNotFound {
        this.walletRepository.findById(id).orElseThrow(WalletNotFound::new);
        this.walletRepository.deleteById(id);
    }

    public List<Wallet> fetchAllWallets(){
        return this.walletRepository.findAll();
    }
}