package com.legalcase.hearingservice.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for representing a list of cases from the Case Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseListResponse {
    
    private List<CaseResponse> cases;
} 