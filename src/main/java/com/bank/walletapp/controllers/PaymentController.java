package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.TransactRequestDto;
import com.bank.walletapp.dtos.PaymentResponseDto;
import com.bank.walletapp.entities.GenericHttpResponse;
import com.bank.walletapp.entities.Payment;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.services.PaymentService;
import com.bank.walletapp.utils.ExceptionUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/wallets/{walletId}/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("")
    public ResponseEntity<GenericHttpResponse> create(Authentication authentication, @PathVariable int walletId, @Valid @RequestBody TransactRequestDto transactRequestDto){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        try {
            Payment payment = this.paymentService.create(walletId, userDetails.getUsername(), transactRequestDto);
            return GenericHttpResponse.create(HttpStatus.CREATED, Message.TRANSACTION_SUCCESSFUL.description, new PaymentResponseDto(payment));
        } catch (Exception e){
            return ExceptionUtils.handle(e);
        }
    }
    @GetMapping("")
    public ResponseEntity<List<PaymentResponseDto>> fetchAll(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<PaymentResponseDto> fetchedList = this.paymentService.fetchAll(userDetails.getUsername()).stream().map(PaymentResponseDto::new).toList();
        return ResponseEntity.ok(fetchedList);
    }
}
