package com.legalcase.client.api.controller;

import com.legalcase.client.application.service.ClientService;
import com.legalcase.client.infrastructure.client.CaseServiceClient;
import com.legalcase.commons.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Müvekkillerin davalarıyla ilgili dış servis entegrasyonlarını yöneten controller
 */
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientCaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientCaseController.class);
    
    private final ClientService clientService;
    private final CaseServiceClient caseServiceClient;
    
    /**
     * Bir müvekkilin tüm davalarını getir
     * Bu endpoint, dava servisinden veri alarak müvekkilin davalarını listeler
     * 
     * @param clientId Müvekkil ID'si
     * @return Dava ID'lerinin listesi
     */
    @GetMapping("/{clientId}/cases")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER', 'ASSISTANT')")
    public ResponseEntity<ApiResponse<List<Long>>> getClientCases(@PathVariable Long clientId) {
        logger.info("REST request to get cases for client ID: {}", clientId);
        
        // Önce müvekkilin var olduğunu kontrol et
        clientService.getClientById(clientId); // Exception fırlatırsa, müvekkil yoktur
        
        // Case servisinden müvekkile ait davaları getir
        List<Long> caseIds = caseServiceClient.getCasesByClientId(clientId);
        
        return ResponseEntity.ok(ApiResponse.success(caseIds));
    }
} 