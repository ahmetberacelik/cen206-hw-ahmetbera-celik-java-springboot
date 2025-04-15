package com.legalcase.caseservice.domain.repository;

import com.legalcase.caseservice.domain.entity.Case;
import com.legalcase.caseservice.domain.valueobject.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing Case entities
 */
@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    
    /**
     * Find a case by its case number
     * 
     * @param caseNumber the case number to search for
     * @return the case with the given case number, if found
     */
    Optional<Case> findByCaseNumber(String caseNumber);
    
    /**
     * Find all cases for a specific client
     * 
     * @param clientId the client ID to search for
     * @return a list of cases for the given client
     */
    List<Case> findByClientId(Long clientId);
    
    /**
     * Find all cases assigned to a specific user
     * 
     * @param userId the user ID to search for
     * @return a list of cases assigned to the given user
     */
    List<Case> findByAssignedUserId(Long userId);
    
    /**
     * Find all cases with a specific status
     * 
     * @param status the status to search for
     * @return a list of cases with the given status
     */
    List<Case> findByStatus(CaseStatus status);
} 