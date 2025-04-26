package com.legalcase.client.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CaseServiceClient {

    private final RestTemplate restTemplate;
    private final Map<String, String> serviceUrls;
    
    /**
     * Bir müvekkilin davalarını getir
     * @param clientId Müvekkil ID'si
     * @return Dava ID'lerinin listesi
     */
    public List<Long> getCasesByClientId(Long clientId) {
        String url = serviceUrls.get("case-service") + "/v1/cases/client/" + clientId;
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // API Response'un içinden data alanını al ve dava ID'lerini çıkar
                Map<String, Object> responseBody = response.getBody();
                
                if (responseBody.containsKey("data") && responseBody.get("data") instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> cases = (List<Map<String, Object>>) responseBody.get("data");
                    
                    return cases.stream()
                            .map(caseMap -> Optional.ofNullable(caseMap.get("id"))
                                    .map(id -> {
                                        if (id instanceof Integer) {
                                            return ((Integer) id).longValue();
                                        } else if (id instanceof Long) {
                                            return (Long) id;
                                        }
                                        return null;
                                    })
                                    .orElse(null))
                            .filter(id -> id != null)
                            .toList();
                }
            }
            
            log.warn("Müvekkil için dava bilgileri getirilemedi, clientId: {}, cevap: {}", clientId, response);
            return Collections.emptyList();
            
        } catch (RestClientException e) {
            log.error("Case servisine bağlanırken hata oluştu, clientId: " + clientId, e);
            return Collections.emptyList();
        }
    }
} 