package com.bank.walletapp.services;

import com.bank.walletapp.entities.Deposit;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.repositories.DepositRepository;
import com.bank.walletapp.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DepositService {
    @Autowired
    private DepositRepository depositRepository;
    @Autowired
    private WalletRepository walletRepository;
    public Deposit create(String username, int walletId, Money amount) throws InvalidAmountPassed, WalletNotFound, UnauthorizedWalletAction {
        Wallet wallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        if (!Objects.equals(username, wallet.getUser().getUsername())) throw new UnauthorizedWalletAction();
        Deposit deposit = new Deposit(wallet, amount);

        return this.depositRepository.save(deposit);
    }
}
