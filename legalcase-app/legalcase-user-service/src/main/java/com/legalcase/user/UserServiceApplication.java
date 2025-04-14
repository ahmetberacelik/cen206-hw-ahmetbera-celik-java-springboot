package com.legalcase.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User Service Main Application
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class UserServiceApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting User Service Application");
        SpringApplication.run(UserServiceApplication.class, args);
        logger.info("User Service Application started successfully");
    }
} 