package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.List;

/**
 * Service interface for managing User entities
 */
public interface IUserService {
    /**
     * Create a new user
     * @param user User entity to create
     * @return ApiResponse containing created user or error
     */
    ApiResponse<User> createUser(User user);

    /**
     * Get user by ID
     * @param id User ID
     * @return ApiResponse containing user or error
     */
    ApiResponse<User> getUserById(Long id);

    /**
     * Get user by username
     * @param username Username to search for
     * @return ApiResponse containing user or error
     */
    ApiResponse<User> getUserByUsername(String username);

    /**
     * Get user by email
     * @param email Email to search for
     * @return ApiResponse containing user or error
     */
    ApiResponse<User> getUserByEmail(String email);

    /**
     * Get user by Keycloak ID
     * @param keycloakId Keycloak ID to search for
     * @return ApiResponse containing user or error
     */
    ApiResponse<User> getUserByKeycloakId(String keycloakId);

    /**
     * Get all users
     * @return ApiResponse containing list of users
     */
    ApiResponse<List<User>> getAllUsers();

    /**
     * Get users by role
     * @param role User role to filter by
     * @return ApiResponse containing list of users with the specified role
     */
    ApiResponse<List<User>> getUsersByRole(UserRole role);

    /**
     * Search users by name or surname
     * @param searchTerm Search term to look for in name or surname
     * @return ApiResponse containing list of matching users
     */
    ApiResponse<List<User>> searchUsers(String searchTerm);

    /**
     * Update an existing user
     * @param user User entity with updated information
     * @return ApiResponse containing updated user or error
     */
    ApiResponse<User> updateUser(User user);

    /**
     * Delete a user
     * @param id User ID to delete
     * @return ApiResponse indicating success or error
     */
    ApiResponse<Void> deleteUser(Long id);

    /**
     * Change user's password
     * @param id User ID
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return ApiResponse containing updated user or error
     */
    ApiResponse<User> changePassword(Long id, String currentPassword, String newPassword);

    /**
     * Enable or disable a user account
     * @param id User ID
     * @param enabled Status to set
     * @return ApiResponse containing updated user or error
     */
    ApiResponse<User> setUserEnabled(Long id, boolean enabled);
}