package com.bank.walletapp.repositories;

import com.bank.walletapp.entities.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Integer> {

//    @Query("SELECT * FROM transactionRecords WHERE sender.owner.username = ?1")
    public List<TransactionRecord> findBySenderUserId(int id);
//    @Query("SELECT * FROM transactionRecords WHERE receiver.owner.username = ?1")
    public List<TransactionRecord> findByReceiverUserId(int id);
}
