package com.bank.walletapp.repositories;

import com.bank.walletapp.entities.Deposit;
import com.bank.walletapp.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {
    public List<Deposit> findAllByUserUsername(String username);
    public void deleteAllByUserUsername(String username);
}
