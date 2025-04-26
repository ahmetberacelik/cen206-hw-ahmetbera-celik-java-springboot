package com.legalcase.client.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ServiceDiscoveryConfig {

    @Value("${service.user.url:http://user-service:8081/api}")
    private String userServiceUrl;
    
    @Value("${service.case.url:http://case-service:8082/api}")
    private String caseServiceUrl;
    
    @Value("${service.hearing.url:http://hearing-service:8083/api}")
    private String hearingServiceUrl;
    
    @Bean
    public Map<String, String> serviceUrls() {
        Map<String, String> urls = new HashMap<>();
        urls.put("user-service", userServiceUrl);
        urls.put("case-service", caseServiceUrl);
        urls.put("hearing-service", hearingServiceUrl);
        return urls;
    }
    
    // Geliştirme ortamı için farklı URL'ler
    @Configuration
    @Profile("dev")
    public static class DevServiceDiscoveryConfig {
        
        @Bean
        public Map<String, String> serviceUrls() {
            Map<String, String> urls = new HashMap<>();
            urls.put("user-service", "http://localhost:8081/api");
            urls.put("case-service", "http://localhost:8082/api");
            urls.put("hearing-service", "http://localhost:8084/api");
            return urls;
        }
    }
} 