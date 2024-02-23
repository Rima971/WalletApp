package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Country;
import com.bank.walletapp.entities.User;
import com.bank.walletapp.exceptions.WalletNotFound;
import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;

@Data
public class UserResponseDto implements ResponseData {
    private String username;
    private Country country;
    private WalletResponseDto wallet;
    public UserResponseDto(User user) throws WalletNotFound {
        this.username = user.getUsername();
        this.country = user.getCountry();
        this.wallet = new WalletResponseDto(user.getWallet());
    }
}

