package com.ahmet.hasan.yakup.esra.legalcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class LegalCaseApp {

    private static final Logger logger = LoggerFactory.getLogger(LegalCaseApp.class);

    public static void main(String[] args) {
        logger.info("Starting Legal Case Management System Application");
        SpringApplication.run(LegalCaseApp.class, args);
        logger.info("Legal Case Management System Application started successfully");
    }

    /**
     * If you want to manually start the console application, activate this profile:
     * -Dspring.profiles.active=console
     */
    @Bean
    @Profile("console")
    public CommandLineRunner runConsoleMode() {
        return args -> {
            logger.info("Console application mode activated. Console application will start automatically.");
        };
    }
}