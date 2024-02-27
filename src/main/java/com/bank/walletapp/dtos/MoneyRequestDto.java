package com.bank.walletapp.dtos;

import com.bank.walletapp.customValidators.ValueOfEnum;
import com.bank.walletapp.entities.Money;
import com.bank.walletapp.enums.Currency;
import com.bank.walletapp.interfaces.ResponseData;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MoneyRequestDto implements ResponseData {
    @NotNull(message = "amount is required")
    @Min(value = 0, message = "amount cannot be negative")
    private double amount;

    @NotNull(message = "currency is required")
    @NotBlank(message = "currency cannot be empty")
    @ValueOfEnum(enumClass = Currency.class, message = "given currency is not supported")
    private String currency;
    public Money getMoney(){
        return new Money(this.amount, Currency.valueOf(this.currency));
    }
}
