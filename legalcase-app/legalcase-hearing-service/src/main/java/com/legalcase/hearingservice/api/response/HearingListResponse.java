package com.legalcase.hearingservice.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response object representing a list of hearings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HearingListResponse {
    
    /**
     * The list of hearing responses.
     */
    private List<HearingResponse> hearings;
    
    /**
     * The total number of hearings in the list.
     */
    private int totalCount;
} 