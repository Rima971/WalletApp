package com.bank.walletapp.repositories;

import com.bank.walletapp.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
}
