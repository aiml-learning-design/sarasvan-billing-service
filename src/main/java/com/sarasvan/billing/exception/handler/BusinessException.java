package com.sarasvan.billing.exception.handler;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}