package com.legalcase.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Gateway route configuration
 */
@Configuration
public class GatewayConfig {
    
    private final Environment environment;
    
    public GatewayConfig(Environment environment) {
        this.environment = environment;
    }
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        boolean isProd = environment.matchesProfiles("prod");
        
        // Servis adresleri için hangi host kullanılacak
        String userServiceUrl = isProd ? "http://user-service:8081" : "http://localhost:8081";
        String appServiceUrl = isProd ? "http://app:8080" : "http://localhost:8080";
        
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r.path("/api/v1/users/**", "/api/v1/auth/**")
                        .uri(userServiceUrl))
                
                // Fallback to monolith during transition
                .route("monolith-fallback", r -> r.path("/api/**")
                        .uri(appServiceUrl))
                
                .build();
    }
}