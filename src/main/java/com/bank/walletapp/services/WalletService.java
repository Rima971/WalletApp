package com.bank.walletapp.services;

import com.bank.walletapp.entities.User;
import com.bank.walletapp.exceptions.*;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    public Money fetchBalanceFromId(String username, int walletId) throws WalletNotFound, UnauthorizedWalletAction {
        Wallet wallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        if (!Objects.equals(username, wallet.getUser().getUsername())) throw new UnauthorizedWalletAction();
        return wallet.getBalance();
    }

    public Wallet create(String username) throws UserNotFound {
        User user = this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        Wallet newWallet = new Wallet(user);
        return this.walletRepository.save(newWallet);
    }

    public List<Wallet> fetchWalletsByUsername(String username){
        return this.walletRepository.findAllByUserUsername(username);
    }

    public void deleteAllByUsername(String username){
        this.walletRepository.deleteAllByUserUsername(username);
    }
}