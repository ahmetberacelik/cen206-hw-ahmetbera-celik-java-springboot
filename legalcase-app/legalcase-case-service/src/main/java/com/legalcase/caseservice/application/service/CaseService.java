package com.legalcase.caseservice.application.service;

import com.legalcase.caseservice.application.dto.CaseDTO;

import java.util.List;

/**
 * Service interface for case operations
 */
public interface CaseService {
    
    /**
     * Get all cases in the system
     * 
     * @return list of all cases
     */
    List<CaseDTO> getAllCases();
    
    /**
     * Get a case by its ID
     * 
     * @param id the ID of the case to retrieve
     * @return the case with the given ID
     */
    CaseDTO getCaseById(Long id);
    
    /**
     * Create a new case
     * 
     * @param caseDTO the case data to create
     * @return the created case
     */
    CaseDTO createCase(CaseDTO caseDTO);
    
    /**
     * Update an existing case
     * 
     * @param id the ID of the case to update
     * @param caseDTO the updated case data
     * @return the updated case
     */
    CaseDTO updateCase(Long id, CaseDTO caseDTO);
    
    /**
     * Delete a case by its ID
     * 
     * @param id the ID of the case to delete
     */
    void deleteCase(Long id);
    
    /**
     * Get all cases for a specific client
     * 
     * @param clientId the ID of the client
     * @return list of cases for the client
     */
    List<CaseDTO> getCasesByClientId(Long clientId);
} 