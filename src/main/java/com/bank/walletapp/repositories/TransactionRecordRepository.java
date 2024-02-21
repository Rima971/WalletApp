package com.bank.walletapp.repositories;

import com.bank.walletapp.entities.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Integer> {

    public List<TransactionRecord> findBySenderUsername(String username);
    public List<TransactionRecord> findByReceiverUsername(String username);
}
