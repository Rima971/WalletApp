package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Country;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.entities.Wallet;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto implements ResponseData {
    private String username;
    private Country country;
    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.country = user.getCountry();
    }
}

