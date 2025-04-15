package com.legalcase.hearingservice.api.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Request object for creating a new hearing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHearingRequest {
    
    /**
     * The ID of the case this hearing is for.
     */
    @NotNull(message = "Case ID is required")
    private Long caseId;
    
    /**
     * The title of the hearing.
     */
    @NotBlank(message = "Title is required")
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
    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    private LocalDateTime scheduledDate;
    
    /**
     * The location where the hearing will take place.
     */
    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must be less than 255 characters")
    private String location;
    
    /**
     * The name of the judge presiding over the hearing.
     */
    @Size(max = 255, message = "Judge name must be less than 255 characters")
    private String judgeName;
    
    /**
     * Additional notes about the hearing.
     */
    @Size(max = 2000, message = "Notes must be less than 2000 characters")
    private String notes;
} 