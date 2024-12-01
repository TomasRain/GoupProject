package com.example.demo.exception.specific;

import com.example.demo.exception.base.ApiException;
import org.springframework.http.HttpStatus;

/**
 * 异常类：产品未找到异常
 */
public class ProductNotFoundException extends ApiException {

    /**
     * 构造器
     *
     * @param message 异常信息
     */
    public ProductNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND.value(), "Product Not Found");
    }
}
