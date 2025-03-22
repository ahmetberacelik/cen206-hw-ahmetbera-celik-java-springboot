package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CaseService implements ICaseService {

    private static final Logger logger = LoggerFactory.getLogger(CaseService.class);

    private final CaseRepository caseRepository;

    @Autowired
    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Override
    public ApiResponse<Case> createCase(Case caseEntity) {
        logger.info("Creating new case: {}", caseEntity.getTitle());
        //Check if the case number is empty
        if (caseEntity.getCaseNumber() == null || caseEntity.getCaseNumber().isEmpty()) {
            return ApiResponse.error("Case number cannot be empty.", HttpStatus.BAD_REQUEST.value());
        }

        //Check if the case number is already in use
        Optional<Case> existingCase = caseRepository.findByCaseNumber(caseEntity.getCaseNumber());
        if (existingCase.isPresent()) {
            //Case number is already in use so give an error to the user
            return ApiResponse.error("Case number '" + caseEntity.getCaseNumber() + "' is already in use.", HttpStatus.CONFLICT.value());
        }

        //Check title is empty
        if (caseEntity.getTitle() == null || caseEntity.getTitle().isEmpty()) {
            return ApiResponse.error("Case title cannot be empty.", HttpStatus.BAD_REQUEST.value());
        }

        //Check status is empty
        if (caseEntity.getStatus() == null) {
            return ApiResponse.error("Case status cannot be empty.", HttpStatus.BAD_REQUEST.value());
        }
        
        Case savedCase = caseRepository.save(caseEntity);
        return ApiResponse.success(savedCase);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Case> getCaseById(Long id) {
        logger.info("Getting case by ID: {}", id);
        Optional<Case> caseOptional = caseRepository.findById(id);
        if (caseOptional.isPresent()) {
            return ApiResponse.success(caseOptional.get());
        } else {
            return ApiResponse.error("Case not found with ID: " + id, HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Case> getCaseByCaseNumber(String caseNumber) {
        logger.info("Getting case by case number: {}", caseNumber);
        Optional<Case> caseOptional = caseRepository.findByCaseNumber(caseNumber);
        if (caseOptional.isPresent()) {
            return ApiResponse.success(caseOptional.get());
        } else {
            return ApiResponse.error("Case not found with number: " + caseNumber, HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Case>> getAllCases() {
        logger.info("Getting all cases");
        List<Case> cases = caseRepository.findAll();
        return ApiResponse.success(cases);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Case>> getCasesByStatus(CaseStatus status) {
        logger.info("Getting cases by status: {}", status);
        List<Case> cases = caseRepository.findByStatus(status);
        return ApiResponse.success(cases);
    }

    @Override
    public ApiResponse<Case> updateCase(Case caseEntity) {
        logger.info("Updating case with ID: {}", caseEntity.getId());

        // Check if case exists
        if (!caseRepository.existsById(caseEntity.getId())) {
            return ApiResponse.error("Case not found with ID: " + caseEntity.getId(), HttpStatus.NOT_FOUND.value());
        }

        // Check if the updated case number conflicts with another case
        Optional<Case> existingCase = caseRepository.findByCaseNumber(caseEntity.getCaseNumber());
        if (existingCase.isPresent() && !existingCase.get().getId().equals(caseEntity.getId())) {
            return ApiResponse.error("Cannot update case: case number '" + caseEntity.getCaseNumber() +
                    "' is already in use by another case.", HttpStatus.CONFLICT.value());
        }

        try {
            Case updatedCase = caseRepository.save(caseEntity);
            return ApiResponse.success(updatedCase);
        } catch (Exception e) {
            logger.error("Error while updating case", e);
            return ApiResponse.error("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Void> deleteCase(Long id) {
        logger.info("Deleting case with ID: {}", id);

        if (!caseRepository.existsById(id)) {
            return ApiResponse.error("Case not found with ID: " + id, HttpStatus.NOT_FOUND.value());
        }

        try {
            caseRepository.deleteById(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            logger.error("Error while deleting case", e);
            return ApiResponse.error("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}