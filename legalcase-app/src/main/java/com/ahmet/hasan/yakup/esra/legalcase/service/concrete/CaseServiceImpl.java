package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.CaseService;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CaseServiceImpl implements CaseService {

    private static final Logger logger = LoggerFactory.getLogger(CaseServiceImpl.class);

    private final CaseRepository caseRepository;

    @Autowired
    public CaseServiceImpl(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Override
    public Case createCase(Case caseEntity) {
        logger.info("Creating new case: {}", caseEntity.getTitle());
        return caseRepository.save(caseEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Case> getCaseById(Long id) {
        logger.info("Getting case by ID: {}", id);
        return caseRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Case> getCaseByCaseNumber(String caseNumber) {
        logger.info("Getting case by case number: {}", caseNumber);
        return caseRepository.findByCaseNumber(caseNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Case> getAllCases() {
        logger.info("Getting all cases");
        return caseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Case> getCasesByStatus(CaseStatus status) {
        logger.info("Getting cases by status: {}", status);
        return caseRepository.findByStatus(status);
    }

    @Override
    public Case updateCase(Case caseEntity) {
        logger.info("Updating case with ID: {}", caseEntity.getId());
        return caseRepository.save(caseEntity);
    }

    @Override
    public void deleteCase(Long id) {
        logger.info("Deleting case with ID: {}", id);
        caseRepository.deleteById(id);
    }
}