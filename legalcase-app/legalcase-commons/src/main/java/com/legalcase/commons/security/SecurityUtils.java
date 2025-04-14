package com.legalcase.commons.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class for security operations
 */
@Component
public class SecurityUtils {

    /**
     * Get the currently authenticated user's username
     *
     * @return the username or empty if not authenticated
     */
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(authentication.getName());
    }
    
    /**
     * Get the current JWT token if it exists
     *
     * @return the JWT token or empty
     */
    public static Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt) {
            return Optional.of((Jwt) principal);
        }
        
        return Optional.empty();
    }
    
    /**
     * Get a claim from the current JWT token
     *
     * @param claimName the name of the claim
     * @return the claim value or empty
     */
    public static Optional<Object> getClaimFromToken(String claimName) {
        Optional<Jwt> jwt = getCurrentJwt();
        
        if (jwt.isEmpty()) {
            return Optional.empty();
        }
        
        Map<String, Object> claims = jwt.get().getClaims();
        return Optional.ofNullable(claims.get(claimName));
    }
    
    /**
     * Check if the current user has the specified role
     *
     * @param role the role to check
     * @return true if the user has the role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_" + role));
    }
} 