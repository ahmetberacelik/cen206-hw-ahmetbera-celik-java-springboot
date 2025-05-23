package com.legalcase.hearingservice.application.service;

import com.legalcase.commons.exception.ResourceNotFoundException;
import com.legalcase.commons.exception.ServiceUnavailableException;
import com.legalcase.commons.exception.ValidationException;
import com.legalcase.hearingservice.application.dto.HearingDTO;
import com.legalcase.hearingservice.application.mapper.HearingMapper;
import com.legalcase.hearingservice.domain.entity.Hearing;
import com.legalcase.hearingservice.domain.repository.HearingRepository;
import com.legalcase.hearingservice.domain.valueobject.HearingStatus;
import com.legalcase.hearingservice.infrastructure.client.CaseServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the HearingService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HearingServiceImpl implements HearingService {

    private final HearingRepository hearingRepository;
    private final HearingMapper hearingMapper;
    private final CaseServiceClient caseServiceClient;

    @Override
    @Transactional(readOnly = true)
    public List<HearingDTO> getAllHearings() {
        log.debug("Getting all hearings");
        return hearingRepository.findAll()
                .stream()
                .map(hearingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HearingDTO getHearingById(Long id) {
        log.debug("Getting hearing with id: {}", id);
        Hearing hearing = findHearingById(id);
        return hearingMapper.toDTO(hearing);
    }

    @Override
    @Transactional
    public HearingDTO createHearing(HearingDTO hearingDTO) {
        log.debug("Creating new hearing with title: {}", hearingDTO.getTitle());
        
        validateHearingData(hearingDTO);
        
        try {
            // Verify that the case exists
            if (!caseServiceClient.caseExists(hearingDTO.getCaseId())) {
                throw new ValidationException("caseId", "Case with ID " + hearingDTO.getCaseId() + " does not exist");
            }
        } catch (ServiceUnavailableException e) {
            // Handle service unavailable exception, perhaps by implementing a fallback
            log.warn("Case service unavailable when creating hearing. Proceeding without case validation.");
        }
        
        // Set default status if not provided
        if (hearingDTO.getStatus() == null) {
            hearingDTO.setStatus(HearingStatus.SCHEDULED);
        }
        
        Hearing hearing = hearingMapper.toEntity(hearingDTO);
        
        // Set creation timestamp
        hearing.setCreatedAt(LocalDateTime.now());
        hearing.setUpdatedAt(LocalDateTime.now());
        
        Hearing savedHearing = hearingRepository.save(hearing);
        return hearingMapper.toDTO(savedHearing);
    }

    @Override
    @Transactional
    public HearingDTO updateHearing(Long id, HearingDTO hearingDTO) {
        log.debug("Updating hearing with id: {}", id);
        
        Hearing existingHearing = findHearingById(id);
        
        // Update fields if provided
        if (hearingDTO.getTitle() != null) {
            existingHearing.setTitle(hearingDTO.getTitle());
        }
        
        if (hearingDTO.getDescription() != null) {
            existingHearing.setDescription(hearingDTO.getDescription());
        }
        
        if (hearingDTO.getScheduledDate() != null) {
            existingHearing.setScheduledDate(hearingDTO.getScheduledDate());
        }
        
        if (hearingDTO.getActualStartTime() != null) {
            existingHearing.setActualStartTime(hearingDTO.getActualStartTime());
        }
        
        if (hearingDTO.getActualEndTime() != null) {
            existingHearing.setActualEndTime(hearingDTO.getActualEndTime());
        }
        
        if (hearingDTO.getLocation() != null) {
            existingHearing.setLocation(hearingDTO.getLocation());
        }
        
        if (hearingDTO.getJudgeName() != null) {
            existingHearing.setJudgeName(hearingDTO.getJudgeName());
        }
        
        if (hearingDTO.getStatus() != null) {
            existingHearing.setStatus(hearingDTO.getStatus());
        }
        
        if (hearingDTO.getNotes() != null) {
            existingHearing.setNotes(hearingDTO.getNotes());
        }
        
        // Update timestamp
        existingHearing.setUpdatedAt(LocalDateTime.now());
        
        Hearing updatedHearing = hearingRepository.save(existingHearing);
        return hearingMapper.toDTO(updatedHearing);
    }

    @Override
    @Transactional
    public void deleteHearing(Long id) {
        log.debug("Deleting hearing with id: {}", id);
        
        if (!hearingRepository.existsById(id)) {
            throw new ResourceNotFoundException(Hearing.class, id);
        }
        
        hearingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HearingDTO> getHearingsByCaseId(Long caseId) {
        log.debug("Getting hearings for case id: {}", caseId);
        
        try {
            // Verify that the case exists before returning hearings
            if (!caseServiceClient.caseExists(caseId)) {
                log.warn("Attempted to get hearings for non-existent case with ID: {}", caseId);
                return List.of(); // Return empty list instead of throwing exception
            }
        } catch (ServiceUnavailableException e) {
            // Handle service unavailable exception with a fallback
            log.warn("Case service unavailable when fetching hearings by case ID. Proceeding with local data only.");
        }
        
        return hearingRepository.findByCaseId(caseId)
                .stream()
                .map(hearingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HearingDTO> getHearingsByStatus(HearingStatus status) {
        log.debug("Getting hearings with status: {}", status);
        
        return hearingRepository.findByStatus(status)
                .stream()
                .map(hearingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HearingDTO> getUpcomingHearings(LocalDateTime date) {
        log.debug("Getting upcoming hearings after: {}", date);
        
        LocalDateTime searchDate = (date != null) ? date : LocalDateTime.now();
        
        return hearingRepository.findByScheduledDateAfter(searchDate)
                .stream()
                .map(hearingMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HearingDTO updateHearingStatus(Long id, HearingStatus status) {
        log.debug("Updating status of hearing with id: {} to {}", id, status);
        
        Hearing hearing = findHearingById(id);
        hearing.setStatus(status);
        
        // If status is COMPLETED, set actual end time if not already set
        if (status == HearingStatus.COMPLETED && hearing.getActualEndTime() == null) {
            hearing.setActualEndTime(LocalDateTime.now());
        }
        
        // If status is IN_PROGRESS, set actual start time if not already set
        if (status == HearingStatus.IN_PROGRESS && hearing.getActualStartTime() == null) {
            hearing.setActualStartTime(LocalDateTime.now());
        }
        
        hearing.setUpdatedAt(LocalDateTime.now());
        
        Hearing updatedHearing = hearingRepository.save(hearing);
        return hearingMapper.toDTO(updatedHearing);
    }
    
    /**
     * Helper method to find a hearing by ID or throw exception.
     * 
     * @param id the hearing ID
     * @return the found hearing entity
     * @throws ResourceNotFoundException if hearing not found
     */
    private Hearing findHearingById(Long id) {
        return hearingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Hearing.class, id));
    }
    
    /**
     * Validate hearing data.
     * 
     * @param hearingDTO the hearing data to validate
     * @throws ValidationException if validation fails
     */
    private void validateHearingData(HearingDTO hearingDTO) {
        if (hearingDTO.getCaseId() == null) {
            throw new ValidationException("caseId", "Case ID is required");
        }
        
        if (hearingDTO.getTitle() == null || hearingDTO.getTitle().trim().isEmpty()) {
            throw new ValidationException("title", "Title is required");
        }
        
        if (hearingDTO.getScheduledDate() == null) {
            throw new ValidationException("scheduledDate", "Scheduled date is required");
        }
        
        if (hearingDTO.getLocation() == null || hearingDTO.getLocation().trim().isEmpty()) {
            throw new ValidationException("location", "Location is required");
        }
    }
}