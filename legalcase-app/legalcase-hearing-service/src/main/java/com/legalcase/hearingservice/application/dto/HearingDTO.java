package com.legalcase.hearingservice.application.dto;

import com.legalcase.hearingservice.domain.valueobject.HearingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Hearing entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HearingDTO {
    private Long id;
    private Long caseId;
    private String title;
    private String description;
    private LocalDateTime scheduledDate;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private String location;
    private String judgeName;
    private HearingStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 