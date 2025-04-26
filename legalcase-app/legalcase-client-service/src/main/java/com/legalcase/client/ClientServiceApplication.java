package com.legalcase.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client Service Main Application
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class ClientServiceApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Client Service Application");
        SpringApplication.run(ClientServiceApplication.class, args);
        logger.info("Client Service Application started successfully");
    }
} 