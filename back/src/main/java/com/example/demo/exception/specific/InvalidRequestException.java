package com.example.demo.exception.specific;

import com.example.demo.exception.base.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value(), "Invalid Request");
    }
}
