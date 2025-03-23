package com.ahmet.hasan.yakup.esra.legalcase.config;

import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.KeycloakAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.UserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for authentication services
 */
@Configuration
public class AuthServiceConfig {

    /**
     * Default authentication service using in-memory authentication
     * Used when auth.provider=local or property is not set
     */
    @Bean("localAuthService")
    @ConditionalOnProperty(name = "auth.provider", havingValue = "local", matchIfMissing = true)
    @Primary
    public IUserAuthenticationService localAuthService(UserAuthenticationService service) {
        return service;
    }

    /**
     * Keycloak authentication service
     * Used when auth.provider=keycloak
     */
    @Bean("primaryAuthService")
    @ConditionalOnProperty(name = "auth.provider", havingValue = "keycloak")
    @Primary
    public IUserAuthenticationService keycloakAuthService(@Qualifier("keycloakAuthService") IUserAuthenticationService service) {
        return service;
    }
}