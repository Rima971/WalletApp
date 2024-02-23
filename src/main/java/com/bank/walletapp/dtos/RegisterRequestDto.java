package com.bank.walletapp.dtos;

import com.bank.walletapp.entities.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto {
    private String username;
    private String password;
    private Country country;
}
