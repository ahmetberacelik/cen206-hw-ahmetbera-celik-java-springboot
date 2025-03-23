package com.ahmet.hasan.yakup.esra.legalcase.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for local development
 * Used when auth.provider=local or property is not set
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = "auth.provider", havingValue = "local", matchIfMissing = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Enable CSRF protection
                .csrf(csrf -> csrf.disable())

                // Allow all requests when using local auth
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )

                .build();
    }
}