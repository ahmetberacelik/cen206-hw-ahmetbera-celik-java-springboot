package com.legalcase.hearingservice.domain.valueobject;

/**
 * Represents the possible statuses of a hearing in the legal case system.
 */
public enum HearingStatus {
    /**
     * The hearing has been scheduled for a future date.
     */
    SCHEDULED,
    
    /**
     * The hearing is currently taking place.
     */
    IN_PROGRESS,
    
    /**
     * The hearing has been completed.
     */
    COMPLETED,
    
    /**
     * The hearing has been postponed to a later date.
     */
    POSTPONED,
    
    /**
     * The hearing has been cancelled and will not be rescheduled.
     */
    CANCELLED
} 