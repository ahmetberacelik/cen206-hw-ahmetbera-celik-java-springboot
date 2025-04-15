package com.legalcase.caseservice.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request class for updating existing cases
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCaseRequest {
    
    private String title;
    
    private String description;
    
    private String status;
    
    private Long assignedUserId;
} 