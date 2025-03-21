package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.CaseService;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
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

    private final CaseService caseService;

    @Autowired
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<Case> createCase(@RequestBody Case caseEntity) {
        logger.info("REST request to create a new case");
        Case createdCase = caseService.createCase(caseEntity);
        return new ResponseEntity<>(createdCase, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Case> getCaseById(@PathVariable Long id) {
        logger.info("REST request to get case by ID: {}", id);
        return caseService.getCaseById(id)
                .map(caseEntity -> new ResponseEntity<>(caseEntity, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Case>> getAllCases() {
        logger.info("REST request to get all cases");
        List<Case> cases = caseService.getAllCases();
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable CaseStatus status) {
        logger.info("REST request to get cases by status: {}", status);
        List<Case> cases = caseService.getCasesByStatus(status);
        return new ResponseEntity<>(cases, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Case> updateCase(@PathVariable Long id, @RequestBody Case caseEntity) {
        logger.info("REST request to update case with ID: {}", id);
        if (!caseEntity.getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Case updatedCase = caseService.updateCase(caseEntity);
        return new ResponseEntity<>(updatedCase, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        logger.info("REST request to delete case with ID: {}", id);
        caseService.deleteCase(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}