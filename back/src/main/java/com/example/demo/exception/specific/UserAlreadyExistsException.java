package com.example.demo.exception.specific;

import com.example.demo.exception.base.ApiException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends ApiException {
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT.value(), "User Already Exists");
    }
}
