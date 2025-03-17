package com.ahmet.hasan.yakup.esra.legalcase.model.enums;

/**
 * @brief Enumeration of case statuses
 * @author Team
 * @date March 2025
 */
public enum CaseStatus {
    /**
     * Newly created case
     */
    NEW,
    
    /**
     * Active case being worked on
     */
    ACTIVE,
    
    /**
     * Case pending action or decision
     */
    PENDING,
    
    /**
     * Closed case (completed)
     */
    CLOSED,
    
    /**
     * Archived case (no longer active but preserved for records)
     */
    ARCHIVED
}
