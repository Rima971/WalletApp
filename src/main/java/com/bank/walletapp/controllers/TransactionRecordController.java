package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.TransactionRecordResponseDto;
import com.bank.walletapp.services.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<TransactionRecordResponseDto>> fetchAll(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<TransactionRecordResponseDto> fetchedList = this.transactionRecordService.fetchAll(userDetails.getUsername()).stream().map(TransactionRecordResponseDto::new).toList();
        return ResponseEntity.ok(fetchedList);
    }
}
