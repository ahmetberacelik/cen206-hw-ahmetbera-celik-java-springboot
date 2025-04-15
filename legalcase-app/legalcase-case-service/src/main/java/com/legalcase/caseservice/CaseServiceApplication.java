package com.legalcase.caseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Spring Boot application class for Case Service
 */
@SpringBootApplication
public class CaseServiceApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(CaseServiceApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Case Service Application");
        SpringApplication.run(CaseServiceApplication.class, args);
        logger.info("Case Service Application started successfully");
    }
} 