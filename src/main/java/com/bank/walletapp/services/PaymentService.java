package com.bank.walletapp.services;

import com.bank.walletapp.dtos.TransactRequestDto;
import com.bank.walletapp.entities.Payment;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.exceptions.InvalidTransactionReceiver;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.UserNotFound;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.repositories.PaymentRepository;
import com.bank.walletapp.repositories.UserRepository;
import com.bank.walletapp.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    public Payment create(int walletId, String username, TransactRequestDto transactRequest) throws WalletNotFound, InvalidTransactionReceiver, UnauthorizedWalletAction {
        if (walletId == transactRequest.getWalletId()) throw new InvalidTransactionReceiver();

        Wallet senderWallet = this.walletRepository.findById(walletId).orElseThrow(WalletNotFound::new);
        Wallet receiverWallet = this.walletRepository.findById(transactRequest.getWalletId()).orElseThrow(WalletNotFound::new);

        if (!Objects.equals(username, senderWallet.getUser().getUsername())) throw new UnauthorizedWalletAction();

        Payment payment = new Payment(senderWallet, receiverWallet, transactRequest.getMoney());

        return this.paymentRepository.save(payment);
    }

    public List<Payment> fetchAll(String username) throws UserNotFound {
        int userId = this.userRepository.findByUsername(username).orElseThrow(UserNotFound::new).getId();
        List<Payment> paymentHistory = new ArrayList<>();
        paymentHistory.addAll(this.paymentRepository.findBySenderUserId(userId));
        paymentHistory.addAll(this.paymentRepository.findByReceiverUserId(userId));
        return paymentHistory;
    }
}
