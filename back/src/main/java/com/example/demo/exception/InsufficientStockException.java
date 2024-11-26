package com.example.demo.exception;

/**
 * 自定义异常类，用于表示库存不足的情况。
 */
public class InsufficientStockException extends RuntimeException {
    
    /**
     * 构造一个新的 InsufficientStockException。
     *
     * @param message 详细的错误信息
     */
    public InsufficientStockException(String message) {
        super(message);
    }
}
