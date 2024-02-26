package com.bank.walletapp.services;

import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.exceptions.UserNotFound;
import com.bank.walletapp.repositories.TransactionRecordRepository;
import com.bank.walletapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionRecordService {
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TransactionRecord> fetchAll(String username) throws UserNotFound {
        int userId = this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new).getId();
        List<TransactionRecord> history = new ArrayList<>();
        history.addAll(this.transactionRecordRepository.findBySenderUserId(userId));
        history.addAll(this.transactionRecordRepository.findByReceiverUserId(userId));
        return history;
    }

    public TransactionRecord add(TransactionRecord transactionRecord){
        return this.transactionRecordRepository.save(transactionRecord);
    }
}
