package com.legalcase.hearingservice.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for representing a case response from the Case Service
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
    private LocalDate openDate;
    private LocalDate closeDate;
    private Long clientId;
    private Long assignedUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 