package com.bank.walletapp.dtos;

import com.bank.walletapp.customValidators.ValueOfEnum;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
public class TransactRequestDto {
    @NotNull(message = "amount is required")
    @Min(value = 0, message = "amount cannot be negative")
    private double amount;

    @NotNull(message = "currency is required")
    @NotBlank(message = "currency is cannot be empth")
    @ValueOfEnum(enumClass = Currency.class)
    private String currency;

    @NotNull(message = "walletId is required")
    @Min(value = 0, message = "walletId cannot be negative")
    private int walletId;

    public Money getMoney(){
        return new Money(this.amount, Currency.valueOf(this.currency));
    }
}
