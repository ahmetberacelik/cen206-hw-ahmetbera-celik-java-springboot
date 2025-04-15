package com.legalcase.caseservice.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * API response class for Case data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseResponse {
    private Long id;
    private String caseNumber;
    private String title;
    private String description;
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate openDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate closeDate;
    
    private Long clientId;
    private String clientName;
    private Long assignedUserId;
    private String assignedUserName;
} 