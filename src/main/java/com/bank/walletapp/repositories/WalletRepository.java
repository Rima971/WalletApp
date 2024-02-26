package com.bank.walletapp.repositories;

import com.bank.walletapp.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    public List<Wallet> findAllByUserUsername(String username);
    public void deleteAllByUserUsername(String username);
}
