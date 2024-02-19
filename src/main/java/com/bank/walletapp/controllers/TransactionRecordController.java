package com.bank.walletapp.controllers;

import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.services.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionRecordController {
    @Autowired
    private TransactionRecordService transactionRecordService;
    @GetMapping("")
    public ResponseEntity<List<TransactionRecord>> fetchAll(){
        return ResponseEntity.ok(this.transactionRecordService.fetchAll());
    }
}
