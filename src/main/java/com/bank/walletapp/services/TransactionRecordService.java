package com.bank.walletapp.services;

import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.repositories.TransactionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionRecordService {
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    public List<TransactionRecord> fetchAll(){
        return this.transactionRecordRepository.findAll();
    }

    public void add(TransactionRecord transactionRecord){
        this.transactionRecordRepository.save(transactionRecord);
    }
}
