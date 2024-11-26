package com.example.demo.exception;

/**
 * 自定义异常类，用于表示产品未找到的情况。
 */
public class ProductNotFoundException extends RuntimeException {
    
    /**
     * 构造一个新的 ProductNotFoundException。
     *
     * @param message 详细的错误信息
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}
