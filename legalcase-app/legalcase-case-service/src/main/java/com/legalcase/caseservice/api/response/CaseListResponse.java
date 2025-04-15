package com.legalcase.caseservice.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Response object for lists of cases
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseListResponse {
    
    @Builder.Default
    private List<CaseResponse> cases = new ArrayList<>();
    
    private Long totalCount;
} 