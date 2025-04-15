package com.legalcase.hearingservice.domain.repository;

import com.legalcase.hearingservice.domain.entity.Hearing;
import com.legalcase.hearingservice.domain.valueobject.HearingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for accessing hearing entities.
 */
@Repository
public interface HearingRepository extends JpaRepository<Hearing, Long> {
    
    /**
     * Find hearings by case ID.
     *
     * @param caseId the ID of the case
     * @return list of hearings for the specified case
     */
    List<Hearing> findByCaseId(Long caseId);
    
    /**
     * Find hearings by status.
     *
     * @param status the status of the hearings to find
     * @return list of hearings with the specified status
     */
    List<Hearing> findByStatus(HearingStatus status);
    
    /**
     * Find upcoming hearings scheduled after the specified date.
     *
     * @param date the date to compare with
     * @return list of hearings scheduled after the specified date
     */
    List<Hearing> findByScheduledDateAfter(LocalDateTime date);
    
    /**
     * Find hearings for a case with a specific status.
     *
     * @param caseId the ID of the case
     * @param status the status of the hearings to find
     * @return list of hearings for the specified case with the specified status
     */
    List<Hearing> findByCaseIdAndStatus(Long caseId, HearingStatus status);
    
    /**
     * Find hearings by location.
     *
     * @param location the location of the hearings
     * @return list of hearings at the specified location
     */
    List<Hearing> findByLocationContaining(String location);
    
    /**
     * Find upcoming hearings for a specific case.
     *
     * @param caseId the ID of the case
     * @param date the date to compare with
     * @return list of hearings for the specified case scheduled after the specified date
     */
    List<Hearing> findByCaseIdAndScheduledDateAfter(Long caseId, LocalDateTime date);
} 