package com.legalcase.commons.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(Class<?> entityClass, Object id) {
        super(String.format("%s with id %s not found", entityClass.getSimpleName(), id));
    }
} 