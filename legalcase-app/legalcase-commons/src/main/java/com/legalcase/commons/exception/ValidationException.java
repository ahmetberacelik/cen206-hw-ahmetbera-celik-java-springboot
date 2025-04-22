package com.legalcase.commons.exception;

/**
 * Exception thrown when a validation error occurs
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message));
    }
} 