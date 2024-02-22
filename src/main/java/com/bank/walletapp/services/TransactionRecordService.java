package com.bank.walletapp.services;

import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.repositories.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionRecordService {
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    public List<TransactionRecord> fetchAll(String username){
        List<TransactionRecord> history = new ArrayList<>();
        history.addAll(this.transactionRecordRepository.findBySenderUsername(username));
        history.addAll(this.transactionRecordRepository.findByReceiverUsername(username));
        return history;
    }

    public TransactionRecord add(TransactionRecord transactionRecord){
        return this.transactionRecordRepository.save(transactionRecord);
    }
}
