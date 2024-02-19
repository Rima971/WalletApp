package com.bank.walletapp.services;

import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.exceptions.*;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordService transactionRecordService;

    public Money getBalanceFromUsername(String username) throws UserNotFound {
        User user = this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        return user.getWallet().getBalance();
    }

    public Wallet createWallet(){
        return this.walletRepository.save(new Wallet());
    }

    public void deposit(String username, Money amount) throws InvalidRequest {
        User user = this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        Wallet wallet = user.getWallet();
        wallet.deposit(amount);

        this.walletRepository.save(wallet);
    }

    public void withdraw(String username, Money amount) throws InvalidRequest, InsuffiucientFunds {
        User user = this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        Wallet wallet = user.getWallet();
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

    public void transact(String fromUsername, String toUsername, Money amount) throws UserNotFound, InsuffiucientFunds{
        User fromUser = this.userRepository.findByUsername(fromUsername).orElseThrow(UserNotFound::new);
        User toUser = this.userRepository.findByUsername(toUsername).orElseThrow(UserNotFound::new);
        Wallet fromWallet = fromUser.getWallet();
        Wallet toWallet = toUser.getWallet();

        fromWallet.transactWith(toWallet, amount);

        this.walletRepository.save(fromWallet);
        this.walletRepository.save(toWallet);

        TransactionRecord transactionRecord = new TransactionRecord(fromUser, toUser, amount);
        this.transactionRecordService.add(transactionRecord);
    }
}