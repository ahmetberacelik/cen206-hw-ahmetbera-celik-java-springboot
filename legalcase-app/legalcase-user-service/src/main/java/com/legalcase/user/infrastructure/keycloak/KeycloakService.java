package com.legalcase.user.infrastructure.keycloak;

import com.legalcase.user.api.dto.request.CreateUserRequest;

import java.util.Set;

/**
 * Service interface for Keycloak operations
 */
public interface KeycloakService {
    
    /**
     * Create a new user in Keycloak
     *
     * @param request the user data
     * @return the Keycloak user ID
     */
    String createUser(CreateUserRequest request);
    
    /**
     * Update an existing user in Keycloak
     *
     * @param keycloakId the Keycloak user ID
     * @param request the updated user data
     */
    void updateUser(String keycloakId, CreateUserRequest request);
    
    /**
     * Delete a user from Keycloak
     *
     * @param keycloakId the Keycloak user ID
     */
    void deleteUser(String keycloakId);
    
    /**
     * Assign roles to a Keycloak user
     *
     * @param keycloakId the Keycloak user ID
     * @param roles the roles to assign
     */
    void assignRolesToUser(String keycloakId, Set<String> roles);
    
    /**
     * Update the roles of a Keycloak user
     *
     * @param keycloakId the Keycloak user ID
     * @param roles the new roles
     */
    void updateUserRoles(String keycloakId, Set<String> roles);
    
    /**
     * Reset a user's password in Keycloak
     *
     * @param keycloakId the Keycloak user ID
     * @param newPassword the new password
     */
    void resetPassword(String keycloakId, String newPassword);
    
    /**
     * Enable or disable a user in Keycloak
     *
     * @param keycloakId the Keycloak user ID
     * @param enabled whether the user should be enabled
     */
    void setUserEnabled(String keycloakId, boolean enabled);
} 