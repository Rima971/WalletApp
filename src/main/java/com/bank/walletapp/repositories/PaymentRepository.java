package com.bank.walletapp.repositories;

import com.bank.walletapp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    public List<Payment> findBySenderUserId(int id);
    public List<Payment> findByReceiverUserId(int id);
}
