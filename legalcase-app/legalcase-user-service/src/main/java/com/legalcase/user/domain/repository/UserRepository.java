package com.legalcase.user.domain.repository;

import com.legalcase.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entities
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find a user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find a user by Keycloak ID
     */
    Optional<User> findByKeycloakId(String keycloakId);
    
    /**
     * Check if a username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if an email exists
     */
    boolean existsByEmail(String email);
} 