package com.example.demo.exception.specific;

import com.example.demo.exception.base.ApiException;
import org.springframework.http.HttpStatus;

/**
 * 异常类：用户未找到异常
 */
public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), "User Not Found");
    }
}
