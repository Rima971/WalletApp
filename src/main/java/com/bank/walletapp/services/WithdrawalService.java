package com.bank.walletapp.services;

import com.bank.walletapp.entities.Deposit;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.entities.Withdrawal;
import com.bank.walletapp.exceptions.InvalidAmountPassed;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.repositories.DepositRepository;
import com.bank.walletapp.repositories.WalletRepository;
import com.bank.walletapp.repositories.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class WithdrawalService {
    @Autowired
    private WithdrawalRepository withdrawalRepository;
    @Autowired
    private WalletRepository walletRepository;
    public Withdrawal create(String username, int walletId, Money amount) throws InvalidAmountPassed, WalletNotFound, UnauthorizedWalletAction {
        Wallet wallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        if (!Objects.equals(username, wallet.getUser().getUsername())) throw new UnauthorizedWalletAction();
        Withdrawal withdrawal = new Withdrawal(wallet, amount);

        return this.withdrawalRepository.save(withdrawal);
    }
}
