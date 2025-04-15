package com.legalcase.hearingservice.application.exception;

/**
 * Exception thrown when a hearing is not found.
 */
public class HearingNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public HearingNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with a detail message constructed from the hearing ID.
     *
     * @param id the ID of the hearing that was not found
     */
    public HearingNotFoundException(Long id) {
        super("Hearing not found with id: " + id);
    }
} 