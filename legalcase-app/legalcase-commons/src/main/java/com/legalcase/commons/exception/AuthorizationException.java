package com.legalcase.commons.exception;

/**
 * Exception thrown when user authorization fails
 */
public class AuthorizationException extends BusinessException {

    public AuthorizationException() {
        super("Unauthorized access");
    }
    
    public AuthorizationException(String message) {
        super(message);
    }
    
    public AuthorizationException(String resource, String operation) {
        super(String.format("Not authorized to %s %s", operation, resource));
    }
} 