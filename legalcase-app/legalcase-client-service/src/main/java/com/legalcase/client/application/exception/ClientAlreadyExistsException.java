package com.legalcase.client.application.exception;

public class ClientAlreadyExistsException extends RuntimeException {
    
    public ClientAlreadyExistsException(String message) {
        super(message);
    }
    
    public ClientAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 