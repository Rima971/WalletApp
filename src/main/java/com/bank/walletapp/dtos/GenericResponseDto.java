package com.bank.walletapp.dtos;

import com.bank.walletapp.interfaces.ResponseData;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class GenericResponseDto {
    private HttpStatus status;
    private int statusCode;
    private String message;
    private ResponseData data;

    private GenericResponseDto(HttpStatus status, String message, ResponseData data){
        this.status = status;
        this.message = message;
        this.data = data;
        this.statusCode = status.value();
    }

    public static ResponseEntity<GenericResponseDto> create(HttpStatus status, String message, ResponseData data){
        return new ResponseEntity<>(new GenericResponseDto(status, message, data), status);
    }
}
