package com.example.demo.exception.specific;

import com.example.demo.exception.base.ApiException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends ApiException {
    public OrderNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), "Order Not Found");
    }
}
