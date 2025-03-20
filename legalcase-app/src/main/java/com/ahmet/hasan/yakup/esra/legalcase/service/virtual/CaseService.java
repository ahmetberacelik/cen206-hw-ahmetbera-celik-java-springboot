package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;

import java.util.List;
import java.util.Optional;

public interface CaseService {
    Case createCase(Case caseEntity);
    Optional<Case> getCaseById(Long id);
    Optional<Case> getCaseByCaseNumber(String caseNumber);
    List<Case> getAllCases();
    List<Case> getCasesByStatus(CaseStatus status);
    Case updateCase(Case caseEntity);
    void deleteCase(Long id);
}