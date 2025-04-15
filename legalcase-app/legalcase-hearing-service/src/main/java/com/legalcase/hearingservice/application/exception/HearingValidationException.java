package com.legalcase.hearingservice.application.exception;

/**
 * Exception thrown when a hearing validation fails.
 */
public class HearingValidationException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public HearingValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public HearingValidationException(String message, Throwable cause) {
        super(message, cause);
    }
} 