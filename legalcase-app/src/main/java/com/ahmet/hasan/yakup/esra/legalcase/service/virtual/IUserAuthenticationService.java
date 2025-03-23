package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.Map;

/**
 * Service interface for authentication operations
 */
public interface IUserAuthenticationService {

    /**
     * Register a new user
     *
     * @param user the user to register
     * @return ApiResponse containing the registered user or error
     */
    ApiResponse<User> registerUser(User user);

    /**
     * Authenticate a user
     *
     * @param usernameOrEmail the username or email
     * @param password the password
     * @return ApiResponse containing authentication data (user and token)
     */
    ApiResponse<Map<String, Object>> authenticateUser(String usernameOrEmail, String password);

    /**
     * Get the current user from token
     *
     * @param token the authentication token
     * @return ApiResponse containing the current user
     */
    ApiResponse<User> getCurrentUser(String token);

    /**
     * Logout the current user
     *
     * @param token the authentication token
     * @return ApiResponse indicating success or error
     */
    ApiResponse<Void> logoutUser(String token);
}