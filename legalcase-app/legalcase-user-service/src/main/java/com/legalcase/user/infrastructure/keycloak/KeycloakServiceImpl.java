package com.legalcase.user.infrastructure.keycloak;

import com.legalcase.commons.exception.BusinessException;
import com.legalcase.user.api.dto.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of KeycloakService using Keycloak Admin Client
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Override
    public String createUser(CreateUserRequest request) {
        log.info("Creating user in Keycloak with username: {}", request.getUsername());
        
        try {
            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEnabled(true);
            user.setEmailVerified(true);
            
            // Set credentials
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            user.setCredentials(Arrays.asList(credential));
            
            // Create user
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            Response response = usersResource.create(user);
            if (response.getStatus() < 200 || response.getStatus() >= 300) {
                throw new BusinessException("Failed to create user in Keycloak: " + response.getStatusInfo().getReasonPhrase());
            }
            
            // Get created user ID
            String keycloakId = CreatedResponseUtil.getCreatedId(response);
            log.info("User created in Keycloak with ID: {}", keycloakId);
            
            return keycloakId;
        } catch (Exception e) {
            log.error("Error creating user in Keycloak", e);
            throw new BusinessException("Failed to create user in Keycloak: " + e.getMessage());
        }
    }
    
    @Override
    public void updateUser(String keycloakId, CreateUserRequest request) {
        log.info("Updating user in Keycloak with ID: {}", keycloakId);
        
        try {
            // Get user resource
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(keycloakId);
            
            // Update user data
            UserRepresentation user = userResource.toRepresentation();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            
            // Update password if provided
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(request.getPassword());
                credential.setTemporary(false);
                userResource.resetPassword(credential);
            }
            
            // Update user
            userResource.update(user);
            log.info("User updated in Keycloak");
        } catch (Exception e) {
            log.error("Error updating user in Keycloak", e);
            throw new BusinessException("Failed to update user in Keycloak: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteUser(String keycloakId) {
        log.info("Deleting user from Keycloak with ID: {}", keycloakId);
        
        try {
            RealmResource realmResource = keycloak.realm(realm);
            realmResource.users().get(keycloakId).remove();
            log.info("User deleted from Keycloak");
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak", e);
            throw new BusinessException("Failed to delete user from Keycloak: " + e.getMessage());
        }
    }
    
    @Override
    public void assignRolesToUser(String keycloakId, Set<String> roles) {
        log.info("Assigning roles to user with ID: {}", keycloakId);
        
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(keycloakId);
            
            // Get all realm roles
            List<RoleRepresentation> availableRoles = realmResource.roles().list();
            
            // Filter roles that are requested and available
            List<RoleRepresentation> rolesToAssign = availableRoles.stream()
                    .filter(role -> roles.contains(role.getName()))
                    .collect(Collectors.toList());
            
            // Assign roles
            userResource.roles().realmLevel().add(rolesToAssign);
            log.info("Roles assigned to user in Keycloak");
        } catch (Exception e) {
            log.error("Error assigning roles to user in Keycloak", e);
            throw new BusinessException("Failed to assign roles to user in Keycloak: " + e.getMessage());
        }
    }
    
    @Override
    public void updateUserRoles(String keycloakId, Set<String> roles) {
        log.info("Updating roles for user with ID: {}", keycloakId);
        
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(keycloakId);
            
            // Get all current roles
            List<RoleRepresentation> currentRoles = userResource.roles().realmLevel().listAll();
            
            // Remove all current roles
            userResource.roles().realmLevel().remove(currentRoles);
            
            // Get all realm roles
            List<RoleRepresentation> availableRoles = realmResource.roles().list();
            
            // Filter roles that are requested and available
            List<RoleRepresentation> rolesToAssign = availableRoles.stream()
                    .filter(role -> roles.contains(role.getName()))
                    .collect(Collectors.toList());
            
            // Assign new roles
            userResource.roles().realmLevel().add(rolesToAssign);
            log.info("Roles updated for user in Keycloak");
        } catch (Exception e) {
            log.error("Error updating roles for user in Keycloak", e);
            throw new BusinessException("Failed to update roles for user in Keycloak: " + e.getMessage());
        }
    }
    
    @Override
    public void resetPassword(String keycloakId, String newPassword) {
        log.info("Resetting password for user with ID: {}", keycloakId);
        
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(keycloakId);
            
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);
            
            userResource.resetPassword(credential);
            log.info("Password reset for user in Keycloak");
        } catch (Exception e) {
            log.error("Error resetting password for user in Keycloak", e);
            throw new BusinessException("Failed to reset password for user in Keycloak: " + e.getMessage());
        }
    }
    
    @Override
    public void setUserEnabled(String keycloakId, boolean enabled) {
        log.info("Setting user enabled status to {} for user with ID: {}", enabled, keycloakId);
        
        try {
            RealmResource realmResource = keycloak.realm(realm);
            UserResource userResource = realmResource.users().get(keycloakId);
            
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            
            userResource.update(user);
            log.info("User enabled status updated in Keycloak");
        } catch (Exception e) {
            log.error("Error setting user enabled status in Keycloak", e);
            throw new BusinessException("Failed to set user enabled status in Keycloak: " + e.getMessage());
        }
    }
} 