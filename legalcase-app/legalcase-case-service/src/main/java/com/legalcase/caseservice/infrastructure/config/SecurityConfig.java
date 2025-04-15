package com.legalcase.caseservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the Case Service API
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                // Swagger endpoints
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Actuator endpoints for health checks
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // API endpoints with role-based security
                .requestMatchers(HttpMethod.GET, "/api/v1/cases/**").hasAnyRole("USER", "ADMIN", "LAWYER")
                .requestMatchers(HttpMethod.POST, "/api/v1/cases/**").hasAnyRole("ADMIN", "LAWYER")
                .requestMatchers(HttpMethod.PUT, "/api/v1/cases/**").hasAnyRole("ADMIN", "LAWYER")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/cases/**").hasRole("ADMIN")
                // Any other request requires authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        
        return http.build();
    }
} 