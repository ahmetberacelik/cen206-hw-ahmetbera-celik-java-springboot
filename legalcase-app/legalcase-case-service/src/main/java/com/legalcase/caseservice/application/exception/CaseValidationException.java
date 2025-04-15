package com.legalcase.caseservice.application.exception;

/**
 * Exception thrown when case validation fails
 */
public class CaseValidationException extends RuntimeException {
    
    /**
     * Constructor with error message
     * 
     * @param message the validation error message
     */
    public CaseValidationException(String message) {
        super(message);
    }
} 