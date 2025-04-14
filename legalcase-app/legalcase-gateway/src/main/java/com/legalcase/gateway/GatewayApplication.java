package com.legalcase.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main API Gateway Application
 */
@SpringBootApplication
public class GatewayApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(GatewayApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Legal Case API Gateway");
        SpringApplication.run(GatewayApplication.class, args);
        logger.info("Legal Case API Gateway started successfully");
    }
} 