package com.legalcase.user.application.service;

import com.legalcase.user.api.dto.request.CreateUserRequest;
import com.legalcase.user.api.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user operations
 */
public interface UserService {
    
    /**
     * Create a new user
     */
    UserResponse createUser(CreateUserRequest request);
    
    /**
     * Get a user by ID
     */
    UserResponse getUserById(Long id);
    
    /**
     * Get a user by username
     */
    UserResponse getUserByUsername(String username);
    
    /**
     * Get a user by email
     */
    UserResponse getUserByEmail(String email);
    
    /**
     * Get a user by Keycloak ID
     */
    Optional<UserResponse> getUserByKeycloakId(String keycloakId);
    
    /**
     * Get all users
     */
    List<UserResponse> getAllUsers();
    
    /**
     * Update a user
     */
    UserResponse updateUser(Long id, CreateUserRequest request);
    
    /**
     * Delete a user
     */
    void deleteUser(Long id);
} 