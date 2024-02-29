package com.bank.walletapp.repositories;

import com.bank.walletapp.entities.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer> {
    public List<Withdrawal> findAllByUserUsername(String username);
    public void deleteAllByUserUsername(String username);
}
