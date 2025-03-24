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
     * Konsol uygulamasını manuel başlatmak istiyorsanız bu profili aktifleştirin:
     * -Dspring.profiles.active=console
     */
    @Bean
    @Profile("console")
    public CommandLineRunner runConsoleMode() {
        return args -> {
            logger.info("Konsol uygulaması modu aktifleştirildi. Konsol uygulaması otomatik olarak başlatılacak.");
        };
    }
}