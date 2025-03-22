package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cases")
public class CaseController {

    private static final Logger logger = LoggerFactory.getLogger(CaseController.class);

    private final ICaseService ICaseService;

    @Autowired
    public CaseController(ICaseService ICaseService) {
        this.ICaseService = ICaseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Case>> createCase(@RequestBody Case caseEntity) {
        logger.info("REST request to create a new case");
        ApiResponse<Case> response = ICaseService.createCase(caseEntity);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Case>> getCaseById(@PathVariable Long id) {
        logger.info("REST request to get case by ID: {}", id);
        ApiResponse<Case> response = ICaseService.getCaseById(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Case>>> getAllCases() {
        logger.info("REST request to get all cases");
        ApiResponse<List<Case>> response = ICaseService.getAllCases();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Case>>> getCasesByStatus(@PathVariable CaseStatus status) {
        logger.info("REST request to get cases by status: {}", status);
        ApiResponse<List<Case>> response = ICaseService.getCasesByStatus(status);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Case>> updateCase(@PathVariable Long id, @RequestBody Case caseEntity) {
        logger.info("REST request to update case with ID: {}", id);
        if (!caseEntity.getId().equals(id)) {
            return new ResponseEntity<>(
                    ApiResponse.error("ID in the URL does not match the ID in the request body", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            );
        }
        ApiResponse<Case> response = ICaseService.updateCase(caseEntity);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCase(@PathVariable Long id) {
        logger.info("REST request to delete case with ID: {}", id);
        ApiResponse<Void> response = ICaseService.deleteCase(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.valueOf(response.getErrorCode()));
    }
}