package com.legalcase.caseservice.api.controller;

import com.legalcase.caseservice.api.request.CreateCaseRequest;
import com.legalcase.caseservice.api.request.UpdateCaseRequest;
import com.legalcase.caseservice.api.response.CaseListResponse;
import com.legalcase.caseservice.api.response.CaseResponse;
import com.legalcase.caseservice.application.dto.CaseDTO;
import com.legalcase.caseservice.application.mapper.CaseMapper;
import com.legalcase.caseservice.application.service.CaseService;
import com.legalcase.caseservice.domain.valueobject.CaseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for case operations
 */
@RestController
@RequestMapping("/api/v1/cases")
@RequiredArgsConstructor
@Slf4j
public class CaseController {
    
    private final CaseService caseService;
    private final CaseMapper caseMapper;
    
    /**
     * Get all cases
     *
     * @return list of all cases
     */
    @GetMapping
    public ResponseEntity<CaseListResponse> getAllCases() {
        log.debug("REST request to get all Cases");
        List<CaseDTO> cases = caseService.getAllCases();
        return ResponseEntity.ok(caseMapper.toListResponse(cases));
    }
    
    /**
     * Get case by ID
     *
     * @param id the ID of the case to retrieve
     * @return the case with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponse> getCaseById(@PathVariable Long id) {
        log.debug("REST request to get Case with id: {}", id);
        CaseDTO caseDTO = caseService.getCaseById(id);
        return ResponseEntity.ok(caseMapper.toResponse(caseDTO));
    }
    
    /**
     * Create a new case
     *
     * @param request the case creation request
     * @return the created case
     */
    @PostMapping
    public ResponseEntity<CaseResponse> createCase(@Valid @RequestBody CreateCaseRequest request) {
        log.debug("REST request to create Case: {}", request);
        
        // Map request to DTO
        CaseDTO caseDTO = CaseDTO.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .clientId(request.getClientId())
                .assignedUserId(request.getAssignedUserId())
                .status(CaseStatus.OPEN)
                .build();
        
        // Create case and return response
        CaseDTO createdCase = caseService.createCase(caseDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(caseMapper.toResponse(createdCase));
    }
    
    /**
     * Update an existing case
     *
     * @param id      the ID of the case to update
     * @param request the case update request
     * @return the updated case
     */
    @PutMapping("/{id}")
    public ResponseEntity<CaseResponse> updateCase(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCaseRequest request) {
        log.debug("REST request to update Case with id: {} and data: {}", id, request);
        
        // Map request to DTO
        CaseDTO caseDTO = CaseDTO.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedUserId(request.getAssignedUserId())
                .build();
        
        // Set status if provided
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                CaseStatus status = CaseStatus.valueOf(request.getStatus());
                caseDTO.setStatus(status);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status provided: {}", request.getStatus());
                // Will be handled by exception handler
                throw new IllegalArgumentException("Invalid case status: " + request.getStatus());
            }
        }
        
        // Update case and return response
        CaseDTO updatedCase = caseService.updateCase(id, caseDTO);
        return ResponseEntity.ok(caseMapper.toResponse(updatedCase));
    }
    
    /**
     * Delete a case
     *
     * @param id the ID of the case to delete
     * @return empty response with 204 status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        log.debug("REST request to delete Case with id: {}", id);
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get cases by client ID
     *
     * @param clientId the ID of the client
     * @return list of cases for the client
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<CaseListResponse> getCasesByClientId(@PathVariable Long clientId) {
        log.debug("REST request to get Cases for client id: {}", clientId);
        List<CaseDTO> cases = caseService.getCasesByClientId(clientId);
        return ResponseEntity.ok(caseMapper.toListResponse(cases));
    }
} 