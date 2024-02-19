package com.bank.walletapp.repositories;

import com.bank.walletapp.entities.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Integer> {
}
