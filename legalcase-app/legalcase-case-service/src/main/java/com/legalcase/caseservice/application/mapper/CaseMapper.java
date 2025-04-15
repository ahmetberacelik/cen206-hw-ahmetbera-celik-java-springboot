package com.legalcase.caseservice.application.mapper;

import com.legalcase.caseservice.api.response.CaseListResponse;
import com.legalcase.caseservice.api.response.CaseResponse;
import com.legalcase.caseservice.application.dto.CaseDTO;
import com.legalcase.caseservice.domain.entity.Case;
import com.legalcase.caseservice.domain.valueobject.CaseStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between different case-related objects
 */
@Component
public class CaseMapper {
    
    /**
     * Converts a Case entity to a CaseDTO
     * 
     * @param entity the Case entity to convert
     * @return the resulting CaseDTO
     */
    public CaseDTO toDTO(Case entity) {
        if (entity == null) {
            return null;
        }
        
        return CaseDTO.builder()
                .id(entity.getId())
                .caseNumber(entity.getCaseNumber())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .openDate(entity.getOpenDate())
                .closeDate(entity.getCloseDate())
                .clientId(entity.getClientId())
                .assignedUserId(entity.getAssignedUserId())
                .build();
    }
    
    /**
     * Converts a CaseDTO to a Case entity
     * 
     * @param dto the CaseDTO to convert
     * @return the resulting Case entity
     */
    public Case toEntity(CaseDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Case entity = new Case();
        entity.setId(dto.getId());
        entity.setCaseNumber(dto.getCaseNumber());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : CaseStatus.OPEN);
        entity.setOpenDate(dto.getOpenDate() != null ? dto.getOpenDate() : LocalDate.now());
        entity.setCloseDate(dto.getCloseDate());
        entity.setClientId(dto.getClientId());
        entity.setAssignedUserId(dto.getAssignedUserId());
        
        return entity;
    }
    
    /**
     * Converts a CaseDTO to a CaseResponse
     * 
     * @param dto the CaseDTO to convert
     * @return the resulting CaseResponse
     */
    public CaseResponse toResponse(CaseDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return CaseResponse.builder()
                .id(dto.getId())
                .caseNumber(dto.getCaseNumber())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus().name())
                .openDate(dto.getOpenDate())
                .closeDate(dto.getCloseDate())
                .clientId(dto.getClientId())
                .clientName(dto.getClientName())
                .assignedUserId(dto.getAssignedUserId())
                .assignedUserName(dto.getAssignedUserName())
                .build();
    }
    
    /**
     * Converts a list of CaseDTOs to a CaseListResponse
     * 
     * @param dtos the list of CaseDTOs to convert
     * @return the resulting CaseListResponse
     */
    public CaseListResponse toListResponse(List<CaseDTO> dtos) {
        List<CaseResponse> responses = dtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return CaseListResponse.builder()
                .cases(responses)
                .totalCount((long) responses.size())
                .build();
    }
} 