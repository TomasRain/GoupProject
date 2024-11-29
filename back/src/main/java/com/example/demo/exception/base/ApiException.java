package com.example.demo.exception.base;

public abstract class ApiException extends RuntimeException {
    private final int status;
    private final String error;

    public ApiException(String message, int status, String error) {
        super(message);
        this.status = status;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
