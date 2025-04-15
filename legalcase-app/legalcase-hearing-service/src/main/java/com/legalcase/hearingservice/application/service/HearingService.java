package com.legalcase.hearingservice.application.service;

import com.legalcase.hearingservice.application.dto.HearingDTO;
import com.legalcase.hearingservice.domain.valueobject.HearingStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for hearing operations.
 */
public interface HearingService {
    
    /**
     * Get all hearings in the system.
     *
     * @return list of all hearings
     */
    List<HearingDTO> getAllHearings();
    
    /**
     * Get a hearing by its ID.
     *
     * @param id the ID of the hearing to retrieve
     * @return the hearing with the given ID
     */
    HearingDTO getHearingById(Long id);
    
    /**
     * Create a new hearing.
     *
     * @param hearingDTO the hearing data to create
     * @return the created hearing
     */
    HearingDTO createHearing(HearingDTO hearingDTO);
    
    /**
     * Update an existing hearing.
     *
     * @param id the ID of the hearing to update
     * @param hearingDTO the updated hearing data
     * @return the updated hearing
     */
    HearingDTO updateHearing(Long id, HearingDTO hearingDTO);
    
    /**
     * Delete a hearing by its ID.
     *
     * @param id the ID of the hearing to delete
     */
    void deleteHearing(Long id);
    
    /**
     * Get all hearings for a specific case.
     *
     * @param caseId the ID of the case
     * @return list of hearings for the case
     */
    List<HearingDTO> getHearingsByCaseId(Long caseId);
    
    /**
     * Get all hearings with a specific status.
     *
     * @param status the status of the hearings to find
     * @return list of hearings with the specified status
     */
    List<HearingDTO> getHearingsByStatus(HearingStatus status);
    
    /**
     * Get all upcoming hearings after the specified date.
     *
     * @param date the date to compare with
     * @return list of hearings scheduled after the specified date
     */
    List<HearingDTO> getUpcomingHearings(LocalDateTime date);
    
    /**
     * Update the status of a hearing.
     *
     * @param id the ID of the hearing to update
     * @param status the new status
     * @return the updated hearing
     */
    HearingDTO updateHearingStatus(Long id, HearingStatus status);
} 