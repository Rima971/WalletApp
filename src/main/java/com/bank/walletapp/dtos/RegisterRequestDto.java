package com.bank.walletapp.dtos;

import com.bank.walletapp.customValidators.ValueOfEnum;
import com.bank.walletapp.entities.Country;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto {
    @NotNull(message = "username is required")
    @NotBlank(message = "username is cannot be empty")
    private String username;

    @NotNull(message = "password is required")
    @NotBlank(message = "password cannot be empty")
    private String password;

    @NotNull(message = "country is required")
    @NotBlank(message = "country is cannot be empty")
    @ValueOfEnum(enumClass = Country.class)
    private String country;

    public Country getCountry(){
        return Country.valueOf(this.country);
    }
}
