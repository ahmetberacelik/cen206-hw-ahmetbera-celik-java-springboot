package com.legalcase.hearingservice.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response object representing a hearing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HearingResponse {
    
    /**
     * The unique identifier of the hearing.
     */
    private Long id;
    
    /**
     * The ID of the case this hearing is for.
     */
    private Long caseId;
    
    /**
     * The title of the hearing.
     */
    private String title;
    
    /**
     * Description of the hearing.
     */
    private String description;
    
    /**
     * The scheduled date and time of the hearing.
     */
    private LocalDateTime scheduledDate;
    
    /**
     * The actual start time of the hearing.
     */
    private LocalDateTime actualStartTime;
    
    /**
     * The actual end time of the hearing.
     */
    private LocalDateTime actualEndTime;
    
    /**
     * The location where the hearing takes place.
     */
    private String location;
    
    /**
     * The name of the judge presiding over the hearing.
     */
    private String judgeName;
    
    /**
     * The status of the hearing.
     * Values: SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED
     */
    private String status;
    
    /**
     * Additional notes about the hearing.
     */
    private String notes;
    
    /**
     * When the hearing record was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * When the hearing record was last updated.
     */
    private LocalDateTime updatedAt;
} 