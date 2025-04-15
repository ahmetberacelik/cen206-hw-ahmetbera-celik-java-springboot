package com.legalcase.caseservice.application.service;

import com.legalcase.caseservice.application.dto.CaseDTO;
import com.legalcase.caseservice.application.exception.CaseNotFoundException;
import com.legalcase.caseservice.application.exception.CaseValidationException;
import com.legalcase.caseservice.application.mapper.CaseMapper;
import com.legalcase.caseservice.domain.entity.Case;
import com.legalcase.caseservice.domain.repository.CaseRepository;
import com.legalcase.caseservice.domain.valueobject.CaseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the CaseService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaseServiceImpl implements CaseService {
    
    private final CaseRepository caseRepository;
    private final CaseMapper caseMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<CaseDTO> getAllCases() {
        log.debug("Getting all cases");
        return caseRepository.findAll()
                .stream()
                .map(caseMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public CaseDTO getCaseById(Long id) {
        log.debug("Getting case with id: {}", id);
        Case caseEntity = findCaseById(id);
        return caseMapper.toDTO(caseEntity);
    }
    
    @Override
    @Transactional
    public CaseDTO createCase(CaseDTO caseDTO) {
        log.debug("Creating new case with title: {}", caseDTO.getTitle());
        
        // Generate a unique case number
        String caseNumber = generateCaseNumber();
        
        // Set default values
        if (caseDTO.getStatus() == null) {
            caseDTO.setStatus(CaseStatus.OPEN);
        }
        
        if (caseDTO.getOpenDate() == null) {
            caseDTO.setOpenDate(LocalDate.now());
        }
        
        // Create the case entity
        Case caseEntity = caseMapper.toEntity(caseDTO);
        caseEntity.setCaseNumber(caseNumber);
        
        // Save and return
        Case savedCase = caseRepository.save(caseEntity);
        return caseMapper.toDTO(savedCase);
    }
    
    @Override
    @Transactional
    public CaseDTO updateCase(Long id, CaseDTO caseDTO) {
        log.debug("Updating case with id: {}", id);
        
        // Find the existing case
        Case existingCase = findCaseById(id);
        
        // Update fields
        if (caseDTO.getTitle() != null) {
            existingCase.setTitle(caseDTO.getTitle());
        }
        
        if (caseDTO.getDescription() != null) {
            existingCase.setDescription(caseDTO.getDescription());
        }
        
        if (caseDTO.getStatus() != null) {
            existingCase.setStatus(caseDTO.getStatus());
            
            // If status is being changed to CLOSED, set close date if not set
            if (caseDTO.getStatus() == CaseStatus.CLOSED && existingCase.getCloseDate() == null) {
                existingCase.setCloseDate(LocalDate.now());
            }
        }
        
        if (caseDTO.getAssignedUserId() != null) {
            existingCase.setAssignedUserId(caseDTO.getAssignedUserId());
        }
        
        // Save and return
        Case updatedCase = caseRepository.save(existingCase);
        return caseMapper.toDTO(updatedCase);
    }
    
    @Override
    @Transactional
    public void deleteCase(Long id) {
        log.debug("Deleting case with id: {}", id);
        
        // Verify case exists
        if (!caseRepository.existsById(id)) {
            throw new CaseNotFoundException(id);
        }
        
        caseRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CaseDTO> getCasesByClientId(Long clientId) {
        log.debug("Getting cases for client id: {}", clientId);
        
        return caseRepository.findByClientId(clientId)
                .stream()
                .map(caseMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Helper method to find a case by ID or throw exception
     * 
     * @param id the case ID
     * @return the found case entity
     * @throws CaseNotFoundException if case not found
     */
    private Case findCaseById(Long id) {
        return caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
    }
    
    /**
     * Generates a unique case number
     * 
     * @return a unique case number string
     */
    private String generateCaseNumber() {
        // Format: CASE-yyyy-randomChars
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        int year = LocalDate.now().getYear();
        return String.format("CASE-%d-%s", year, randomPart);
    }
} 