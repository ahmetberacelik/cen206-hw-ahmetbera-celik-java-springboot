package com.legalcase.caseservice.application.exception;

/**
 * Exception thrown when a requested case cannot be found
 */
public class CaseNotFoundException extends RuntimeException {
    
    /**
     * Constructor for case not found by ID
     * 
     * @param id the ID of the case that was not found
     */
    public CaseNotFoundException(Long id) {
        super("Case not found with id: " + id);
    }
    
    /**
     * Constructor with custom message
     * 
     * @param message the error message
     */
    public CaseNotFoundException(String message) {
        super(message);
    }
} 