package com.legalcase.caseservice.application.dto;

import com.legalcase.caseservice.domain.valueobject.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for Case entities
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseDTO {
    private Long id;
    private String caseNumber;
    private String title;
    private String description;
    private CaseStatus status;
    private LocalDate openDate;
    private LocalDate closeDate;
    private Long clientId;
    private String clientName;
    private Long assignedUserId;
    private String assignedUserName;
} 