package com.legalcase.commons.exception;

/**
 * Exception thrown when a service is unavailable
 */
public class ServiceUnavailableException extends BusinessException {
    
    public ServiceUnavailableException(String serviceName) {
        super(String.format("Service '%s' is currently unavailable", serviceName));
    }
    
    public ServiceUnavailableException(String serviceName, String reason) {
        super(String.format("Service '%s' is currently unavailable: %s", serviceName, reason));
    }
    
    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super(String.format("Service '%s' is currently unavailable", serviceName), cause);
    }
} 