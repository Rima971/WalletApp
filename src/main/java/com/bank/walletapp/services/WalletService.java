package com.bank.walletapp.services;

import com.bank.walletapp.dtos.TransactRequestDto;
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
import java.util.Objects;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordService transactionRecordService;

    public Money getBalanceFromId(String username, int walletId) throws WalletNotFound, UnauthorizedWalletAction {
        Wallet wallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        if (!Objects.equals(username, wallet.getUser().getUsername())) throw new UnauthorizedWalletAction();
        return wallet.getBalance();
    }

    public Wallet addWallet(String username) throws UserNotFound {
        User user = this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new);
        Wallet newWallet = new Wallet(user);
        return this.walletRepository.save(newWallet);
    }

    public Wallet deposit(String username, int walletId, Money amount) throws InvalidAmountPassed, WalletNotFound, UnauthorizedWalletAction {
        Wallet wallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        if (!Objects.equals(username, wallet.getUser().getUsername())) throw new UnauthorizedWalletAction();
        wallet.deposit(amount);

        return this.walletRepository.save(wallet);
    }

    public Wallet withdraw(String username, int walletId, Money amount) throws InvalidAmountPassed, InsufficientFunds, WalletNotFound, UnauthorizedWalletAction {
        Wallet wallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        if (!Objects.equals(username, wallet.getUser().getUsername())) throw new UnauthorizedWalletAction();
        wallet.withdraw(amount);

        return this.walletRepository.save(wallet);
    }

    public List<Wallet> fetchWalletsByUsername(String username){
        return this.walletRepository.findAllByUserUsername(username);
    }

    public TransactionRecord transact(int walletId, String username, TransactRequestDto transactRequest) throws InsufficientFunds, WalletNotFound, UnauthorizedWalletAction {
        if (walletId == transactRequest.getWalletId()) throw new InvalidTransactionReceiver();

        Wallet senderWallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        Wallet receiverWallet = this.walletRepository.findById(transactRequest.getWalletId()).orElseThrow(WalletNotFound::new);

        if (!Objects.equals(username, senderWallet.getUser().getUsername())) throw new UnauthorizedWalletAction();

        senderWallet.transactWith(receiverWallet, transactRequest.getMoney());

        this.walletRepository.save(senderWallet);
        this.walletRepository.save(receiverWallet);

        TransactionRecord transactionRecord = new TransactionRecord(senderWallet, receiverWallet, transactRequest.getMoney());
        return this.transactionRecordService.add(transactionRecord);
    }

    public void deleteAllByUsername(String username){
        this.walletRepository.deleteAllByUserUsername(username);
    }
}