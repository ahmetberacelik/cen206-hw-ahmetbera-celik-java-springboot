package com.legalcase.hearingservice.application.mapper;

import com.legalcase.hearingservice.api.request.CreateHearingRequest;
import com.legalcase.hearingservice.api.request.UpdateHearingRequest;
import com.legalcase.hearingservice.api.response.HearingListResponse;
import com.legalcase.hearingservice.api.response.HearingResponse;
import com.legalcase.hearingservice.application.dto.HearingDTO;
import com.legalcase.hearingservice.domain.entity.Hearing;
import com.legalcase.hearingservice.domain.valueobject.HearingStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Hearing entity, DTO, requests, and responses.
 */
@Component
public class HearingMapper {

    /**
     * Convert a Hearing entity to a HearingDTO.
     *
     * @param hearing the hearing entity
     * @return the hearing DTO
     */
    public HearingDTO toDTO(Hearing hearing) {
        if (hearing == null) {
            return null;
        }
        
        return HearingDTO.builder()
                .id(hearing.getId())
                .caseId(hearing.getCaseId())
                .title(hearing.getTitle())
                .description(hearing.getDescription())
                .scheduledDate(hearing.getScheduledDate())
                .actualStartTime(hearing.getActualStartTime())
                .actualEndTime(hearing.getActualEndTime())
                .location(hearing.getLocation())
                .judgeName(hearing.getJudgeName())
                .status(hearing.getStatus())
                .notes(hearing.getNotes())
                .createdAt(hearing.getCreatedAt())
                .updatedAt(hearing.getUpdatedAt())
                .build();
    }

    /**
     * Convert a HearingDTO to a Hearing entity.
     *
     * @param hearingDTO the hearing DTO
     * @return the hearing entity
     */
    public Hearing toEntity(HearingDTO hearingDTO) {
        if (hearingDTO == null) {
            return null;
        }
        
        return Hearing.builder()
                .id(hearingDTO.getId())
                .caseId(hearingDTO.getCaseId())
                .title(hearingDTO.getTitle())
                .description(hearingDTO.getDescription())
                .scheduledDate(hearingDTO.getScheduledDate())
                .actualStartTime(hearingDTO.getActualStartTime())
                .actualEndTime(hearingDTO.getActualEndTime())
                .location(hearingDTO.getLocation())
                .judgeName(hearingDTO.getJudgeName())
                .status(hearingDTO.getStatus())
                .notes(hearingDTO.getNotes())
                .build();
    }

    /**
     * Convert a HearingDTO to a HearingResponse.
     *
     * @param hearingDTO the hearing DTO
     * @return the hearing response
     */
    public HearingResponse toResponse(HearingDTO hearingDTO) {
        if (hearingDTO == null) {
            return null;
        }
        
        return HearingResponse.builder()
                .id(hearingDTO.getId())
                .caseId(hearingDTO.getCaseId())
                .title(hearingDTO.getTitle())
                .description(hearingDTO.getDescription())
                .scheduledDate(hearingDTO.getScheduledDate())
                .actualStartTime(hearingDTO.getActualStartTime())
                .actualEndTime(hearingDTO.getActualEndTime())
                .location(hearingDTO.getLocation())
                .judgeName(hearingDTO.getJudgeName())
                .status(hearingDTO.getStatus().name())
                .notes(hearingDTO.getNotes())
                .createdAt(hearingDTO.getCreatedAt())
                .updatedAt(hearingDTO.getUpdatedAt())
                .build();
    }

    /**
     * Convert a list of HearingDTOs to a HearingListResponse.
     *
     * @param hearingDTOs the list of hearing DTOs
     * @return the hearing list response
     */
    public HearingListResponse toListResponse(List<HearingDTO> hearingDTOs) {
        if (hearingDTOs == null) {
            return new HearingListResponse(List.of(), 0);
        }
        
        List<HearingResponse> hearingResponses = hearingDTOs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return new HearingListResponse(hearingResponses, hearingResponses.size());
    }

    /**
     * Convert a CreateHearingRequest to a HearingDTO.
     *
     * @param request the create hearing request
     * @return the hearing DTO
     */
    public HearingDTO fromCreateRequest(CreateHearingRequest request) {
        if (request == null) {
            return null;
        }
        
        return HearingDTO.builder()
                .caseId(request.getCaseId())
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledDate(request.getScheduledDate())
                .location(request.getLocation())
                .judgeName(request.getJudgeName())
                .status(HearingStatus.SCHEDULED) // Default status for new hearings
                .notes(request.getNotes())
                .build();
    }

    /**
     * Apply an UpdateHearingRequest to a HearingDTO.
     *
     * @param dto the hearing DTO to update
     * @param request the update hearing request
     * @return the updated hearing DTO
     */
    public HearingDTO applyUpdateRequest(HearingDTO dto, UpdateHearingRequest request) {
        if (request == null || dto == null) {
            return dto;
        }
        
        if (request.getTitle() != null) {
            dto.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            dto.setDescription(request.getDescription());
        }
        
        if (request.getScheduledDate() != null) {
            dto.setScheduledDate(request.getScheduledDate());
        }
        
        if (request.getActualStartTime() != null) {
            dto.setActualStartTime(request.getActualStartTime());
        }
        
        if (request.getActualEndTime() != null) {
            dto.setActualEndTime(request.getActualEndTime());
        }
        
        if (request.getLocation() != null) {
            dto.setLocation(request.getLocation());
        }
        
        if (request.getJudgeName() != null) {
            dto.setJudgeName(request.getJudgeName());
        }
        
        if (request.getStatus() != null) {
            try {
                dto.setStatus(HearingStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                // Status handling will be in the service
            }
        }
        
        if (request.getNotes() != null) {
            dto.setNotes(request.getNotes());
        }
        
        return dto;
    }
} 