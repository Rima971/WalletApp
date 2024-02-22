package com.bank.walletapp.controllers;

import com.bank.walletapp.authentication.CustomUserDetails;
import com.bank.walletapp.dtos.*;
import com.bank.walletapp.entities.TransactionRecord;
import com.bank.walletapp.enums.Message;
import com.bank.walletapp.exceptions.InsufficientFunds;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.exceptions.InvalidTransactionReceiver;
import com.bank.walletapp.exceptions.UnauthorizedWalletAction;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.interfaces.ResponseData;
import com.bank.walletapp.services.UserService;
import com.bank.walletapp.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @GetMapping("")
    public ResponseEntity<List<BalanceResponseDto>> fetchAllWallets(){
        List<BalanceResponseDto> wallets = this.walletService.fetchAllWallets().stream().map(wallet->new BalanceResponseDto(wallet.getBalance())).toList();
        return new ResponseEntity<>(wallets, HttpStatus.OK);
    }

    @PutMapping("/{walletId}/deposit")
    public ResponseEntity<GenericResponseDto> deposit(Authentication authentication, @PathVariable int walletId, @RequestBody Money amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            Wallet updatedWallet = this.walletService.deposit(userDetails.getUsername(), walletId, amount);
            Money balance = updatedWallet.getBalance();
            return GenericResponseDto.create(HttpStatus.OK, Message.WALLET_SUCCESSFUL_DEPOSIT.description, new BalanceResponseDto(balance));
        } catch (WalletNotFound e){
            return GenericResponseDto.create(HttpStatus.CONFLICT, Message.WALLET_NOT_FOUND.description, null);
        } catch (UnauthorizedWalletAction e){
            return GenericResponseDto.create(HttpStatus.UNAUTHORIZED, Message.WALLET_UNAUTHORIZED_USER_ACTION.description, null);
        } catch (Exception e){
            return GenericResponseDto.create(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }

    @PutMapping("/{walletId}/withdraw")
    public ResponseEntity<GenericResponseDto> withdraw(Authentication authentication, @PathVariable int walletId, @RequestBody Money amount) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            Wallet updatedWallet = this.walletService.withdraw(userDetails.getUsername(), walletId, amount);
            Money balance = updatedWallet.getBalance();
            return GenericResponseDto.create(HttpStatus.OK, Message.WALLET_SUCCESSFUL_WITHDRAWAL.description, new BalanceResponseDto(balance));
        } catch (InsufficientFunds e){
            return GenericResponseDto.create(HttpStatus.BAD_REQUEST, Message.WALLET_INSUFFICIENT_FUNDS.description, null);
        } catch (WalletNotFound e){
            return GenericResponseDto.create(HttpStatus.CONFLICT, Message.WALLET_NOT_FOUND.description, null);
        } catch (UnauthorizedWalletAction e){
            return GenericResponseDto.create(HttpStatus.UNAUTHORIZED, Message.WALLET_UNAUTHORIZED_USER_ACTION.description, null);
        } catch (Exception e){
            return GenericResponseDto.create(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }

    }

    @PutMapping("/{walletId}/transact")
    public ResponseEntity<GenericResponseDto> transact(Authentication authentication, @PathVariable int walletId, @RequestBody TransactRequestDto transactRequestDto){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        try{
            TransactionRecord record = this.walletService.transact(walletId, userDetails.getUsername(), transactRequestDto.getUsername(), transactRequestDto.getMoney());
            return GenericResponseDto.create(HttpStatus.OK, Message.WALLETS_SUCCESSFUL_TRANSACTION.description, new TransactionRecordResponseDto(record));
        } catch (UnauthorizedWalletAction e){
             return GenericResponseDto.create(HttpStatus.UNAUTHORIZED, Message.WALLET_UNAUTHORIZED_USER_ACTION.description, null);
        } catch (InsufficientFunds e){
            return GenericResponseDto.create(HttpStatus.BAD_REQUEST, Message.WALLET_INSUFFICIENT_FUNDS.description, null);
        } catch (WalletNotFound e){
            return GenericResponseDto.create(HttpStatus.CONFLICT, Message.WALLET_NOT_FOUND.description, null);
        } catch (InvalidTransactionReceiver e){
            return GenericResponseDto.create(HttpStatus.BAD_REQUEST, Message.WALLET_INVALID_TRANSACTION_RECEIVER.description, null);
        } catch (Exception e){
            return GenericResponseDto.create(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
        }
    }
}
