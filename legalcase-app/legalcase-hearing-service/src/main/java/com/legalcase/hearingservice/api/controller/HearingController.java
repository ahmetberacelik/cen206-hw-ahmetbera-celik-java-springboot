package com.legalcase.hearingservice.api.controller;

import com.legalcase.hearingservice.api.request.CreateHearingRequest;
import com.legalcase.hearingservice.api.request.UpdateHearingRequest;
import com.legalcase.hearingservice.api.response.HearingListResponse;
import com.legalcase.hearingservice.api.response.HearingResponse;
import com.legalcase.hearingservice.application.dto.HearingDTO;
import com.legalcase.hearingservice.application.mapper.HearingMapper;
import com.legalcase.hearingservice.application.service.HearingService;
import com.legalcase.hearingservice.domain.valueobject.HearingStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * REST controller for hearing operations.
 */
@RestController
@RequestMapping("/api/v1/hearings")
@RequiredArgsConstructor
@Slf4j
public class HearingController {
    
    private final HearingService hearingService;
    private final HearingMapper hearingMapper;
    
    /**
     * Get all hearings.
     *
     * @return list of all hearings
     */
    @GetMapping
    public ResponseEntity<HearingListResponse> getAllHearings() {
        log.debug("REST request to get all Hearings");
        return ResponseEntity.ok(hearingMapper.toListResponse(hearingService.getAllHearings()));
    }
    
    /**
     * Get a hearing by ID.
     *
     * @param id the ID of the hearing to retrieve
     * @return the hearing with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HearingResponse> getHearingById(@PathVariable Long id) {
        log.debug("REST request to get Hearing with id: {}", id);
        HearingDTO hearingDTO = hearingService.getHearingById(id);
        return ResponseEntity.ok(hearingMapper.toResponse(hearingDTO));
    }
    
    /**
     * Create a new hearing.
     *
     * @param request the hearing creation request
     * @return the created hearing
     */
    @PostMapping
    public ResponseEntity<HearingResponse> createHearing(@Valid @RequestBody CreateHearingRequest request) {
        log.debug("REST request to create Hearing: {}", request);
        
        HearingDTO hearingDTO = hearingMapper.fromCreateRequest(request);
        HearingDTO createdHearing = hearingService.createHearing(hearingDTO);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hearingMapper.toResponse(createdHearing));
    }
    
    /**
     * Update an existing hearing.
     *
     * @param id the ID of the hearing to update
     * @param request the hearing update request
     * @return the updated hearing
     */
    @PutMapping("/{id}")
    public ResponseEntity<HearingResponse> updateHearing(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHearingRequest request) {
        log.debug("REST request to update Hearing with id: {} and data: {}", id, request);
        
        HearingDTO existingHearingDTO = hearingService.getHearingById(id);
        HearingDTO updatedDTO = hearingMapper.applyUpdateRequest(existingHearingDTO, request);
        
        HearingDTO updatedHearing = hearingService.updateHearing(id, updatedDTO);
        return ResponseEntity.ok(hearingMapper.toResponse(updatedHearing));
    }
    
    /**
     * Delete a hearing.
     *
     * @param id the ID of the hearing to delete
     * @return empty response with 204 status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHearing(@PathVariable Long id) {
        log.debug("REST request to delete Hearing with id: {}", id);
        hearingService.deleteHearing(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get hearings by case ID.
     *
     * @param caseId the ID of the case
     * @return list of hearings for the case
     */
    @GetMapping("/case/{caseId}")
    public ResponseEntity<HearingListResponse> getHearingsByCaseId(@PathVariable Long caseId) {
        log.debug("REST request to get Hearings for case id: {}", caseId);
        return ResponseEntity.ok(hearingMapper.toListResponse(hearingService.getHearingsByCaseId(caseId)));
    }
    
    /**
     * Get hearings by status.
     *
     * @param status the status of the hearings to find
     * @return list of hearings with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<HearingListResponse> getHearingsByStatus(@PathVariable String status) {
        log.debug("REST request to get Hearings with status: {}", status);
        
        try {
            HearingStatus hearingStatus = HearingStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(hearingMapper.toListResponse(hearingService.getHearingsByStatus(hearingStatus)));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get upcoming hearings.
     *
     * @param fromDate optional date to start from (defaults to now)
     * @return list of upcoming hearings
     */
    @GetMapping("/upcoming")
    public ResponseEntity<HearingListResponse> getUpcomingHearings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate) {
        log.debug("REST request to get upcoming Hearings from date: {}", fromDate);
        
        LocalDateTime date = (fromDate != null) ? fromDate : LocalDateTime.now();
        return ResponseEntity.ok(hearingMapper.toListResponse(hearingService.getUpcomingHearings(date)));
    }
    
    /**
     * Update the status of a hearing.
     *
     * @param id the ID of the hearing to update
     * @param status the new status
     * @return the updated hearing
     */
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<HearingResponse> updateHearingStatus(
            @PathVariable Long id,
            @PathVariable String status) {
        log.debug("REST request to update status of Hearing with id: {} to {}", id, status);
        
        try {
            HearingStatus hearingStatus = HearingStatus.valueOf(status.toUpperCase());
            HearingDTO updatedHearing = hearingService.updateHearingStatus(id, hearingStatus);
            return ResponseEntity.ok(hearingMapper.toResponse(updatedHearing));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }
} 