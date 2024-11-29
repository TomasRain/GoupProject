package com.example.demo.exception.specific;

import com.example.demo.exception.base.ApiException;
import org.springframework.http.HttpStatus;

public class InsufficientStockException extends ApiException {
    public InsufficientStockException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value(), "Insufficient Stock");
    }
}
