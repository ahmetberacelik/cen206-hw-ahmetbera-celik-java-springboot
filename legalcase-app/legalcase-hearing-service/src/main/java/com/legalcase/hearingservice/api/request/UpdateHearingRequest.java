package com.legalcase.hearingservice.api.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request object for updating an existing hearing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHearingRequest {
    
    /**
     * The title of the hearing.
     */
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;
    
    /**
     * Description of the hearing.
     */
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
    
    /**
     * The scheduled date and time of the hearing.
     */
    @Future(message = "Scheduled date must be in the future")
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
     * The location where the hearing will take place.
     */
    @Size(max = 255, message = "Location must be less than 255 characters")
    private String location;
    
    /**
     * The name of the judge presiding over the hearing.
     */
    @Size(max = 255, message = "Judge name must be less than 255 characters")
    private String judgeName;
    
    /**
     * The status of the hearing.
     * Valid values: SCHEDULED, IN_PROGRESS, COMPLETED, POSTPONED, CANCELLED
     */
    @Size(max = 20, message = "Status must be valid")
    private String status;
    
    /**
     * Additional notes about the hearing.
     */
    @Size(max = 2000, message = "Notes must be less than 2000 characters")
    private String notes;
} 