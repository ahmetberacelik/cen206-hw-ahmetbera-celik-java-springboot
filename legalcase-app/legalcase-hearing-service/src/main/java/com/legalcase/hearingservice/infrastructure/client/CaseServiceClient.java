package com.legalcase.hearingservice.infrastructure.client;

import com.legalcase.commons.dto.ApiResponse;
import com.legalcase.commons.exception.ServiceUnavailableException;
import com.legalcase.hearingservice.infrastructure.client.dto.CaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for communicating with the Case Service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CaseServiceClient {

    private final RestTemplate restTemplate;
    
    @Value("${services.case-service.url}")
    private String caseServiceBaseUrl;
    
    /**
     * Get a case by its ID
     *
     * @param caseId the ID of the case to retrieve
     * @return the case if found, or null if not found or an error occurs
     * @throws ServiceUnavailableException if the case service is unavailable
     */
    public CaseResponse getCaseById(Long caseId) {
        String url = caseServiceBaseUrl + "/api/v1/cases/" + caseId;
        log.debug("Requesting case with ID {} from case service: {}", caseId, url);
        
        try {
            ResponseEntity<ApiResponse<CaseResponse>> response = restTemplate.getForEntity(
                    url,
                    getApiResponseType()
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().isSuccess()) {
                log.debug("Successfully retrieved case with ID {}", caseId);
                return response.getBody().getData();
            } else {
                log.warn("Failed to retrieve case with ID {}: {}", 
                        caseId, 
                        response.getBody() != null ? response.getBody().getMessage() : "No response body");
                return null;
            }
        } catch (RestClientException e) {
            log.error("Error calling case service for case with ID {}: {}", caseId, e.getMessage());
            throw new ServiceUnavailableException("Case Service", e);
        }
    }
    
    /**
     * Check if a case exists by its ID
     *
     * @param caseId the ID of the case to check
     * @return true if the case exists, false otherwise
     * @throws ServiceUnavailableException if the case service is unavailable
     */
    public boolean caseExists(Long caseId) {
        try {
            CaseResponse caseResponse = getCaseById(caseId);
            return caseResponse != null;
        } catch (ServiceUnavailableException e) {
            // If configured, we could implement a fallback mechanism or circuit breaker here
            log.error("Case service unavailable when checking case exists: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Helper method to get the correct generic type for the API response
     */
    @SuppressWarnings("unchecked")
    private Class<ApiResponse<CaseResponse>> getApiResponseType() {
        return (Class<ApiResponse<CaseResponse>>) (Class<?>) ApiResponse.class;
    }
} 