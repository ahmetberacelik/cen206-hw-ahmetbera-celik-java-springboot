package com.ahmet.hasan.yakup.esra.legalcase.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Enable CSRF protection
                .csrf(csrf -> csrf.disable())

                // Allow all requests
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )

                .build();
    }
}