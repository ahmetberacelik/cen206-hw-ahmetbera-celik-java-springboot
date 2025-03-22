package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.List;

public interface ICaseService {
    ApiResponse<Case> createCase(Case caseEntity);
    ApiResponse<Case> getCaseById(Long id);
    ApiResponse<Case> getCaseByCaseNumber(String caseNumber);
    ApiResponse<List<Case>> getAllCases();
    ApiResponse<List<Case>> getCasesByStatus(CaseStatus status);
    ApiResponse<Case> updateCase(Case caseEntity);
    ApiResponse<Void> deleteCase(Long id);
}