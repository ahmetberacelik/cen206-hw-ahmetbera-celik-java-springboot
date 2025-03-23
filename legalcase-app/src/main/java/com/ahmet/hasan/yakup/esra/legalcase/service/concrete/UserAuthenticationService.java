package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.repository.UserRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the UserAuthenticationService interface
 * This is a simple implementation for testing purposes before Keycloak integration
 */
@Service
@Transactional
public class UserAuthenticationService implements IUserAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthenticationService.class);

    // In-memory token store - would be replaced with proper JWT or other token mechanism
    private static final Map<String, Long> tokenUserMap = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository,
                                     IUserService userService,
                                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<User> registerUser(User user) {
        logger.info("Registering new user with username: {}", user.getUsername());
        return userService.createUser(user);
    }

    @Override
    public ApiResponse<Map<String, Object>> authenticateUser(String usernameOrEmail, String password) {
        logger.info("Authenticating user: {}", usernameOrEmail);

        // Try to find user by username
        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);

        // If not found, try by email
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }

        // Check if user exists
        if (userOptional.isEmpty()) {
            return ApiResponse.error("Invalid username/email or password",
                    HttpStatus.UNAUTHORIZED.value());
        }

        User user = userOptional.get();

        // Check if account is enabled
        if (!user.isEnabled()) {
            return ApiResponse.error("Account is disabled",
                    HttpStatus.UNAUTHORIZED.value());
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ApiResponse.error("Invalid username/email or password",
                    HttpStatus.UNAUTHORIZED.value());
        }

        try {
            // Generate a token (simple UUID for testing)
            String token = UUID.randomUUID().toString();

            // Store token with user ID
            tokenUserMap.put(token, user.getId());

            // Create response with user and token
            Map<String, Object> response = new HashMap<>();
            user.setPassword(null); // Clear password for security
            response.put("user", user);
            response.put("token", token);

            return ApiResponse.success(response);
        } catch (Exception e) {
            logger.error("Error during user authentication: {}", e.getMessage(), e);
            return ApiResponse.error("Authentication failed: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<User> getCurrentUser(String token) {
        logger.info("Getting current user from token");

        if (token == null || token.isEmpty()) {
            return ApiResponse.error("Authentication token is required",
                    HttpStatus.UNAUTHORIZED.value());
        }

        // Extract token from "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Check if token exists
        Long userId = tokenUserMap.get(token);
        if (userId == null) {
            return ApiResponse.error("Invalid or expired token",
                    HttpStatus.UNAUTHORIZED.value());
        }

        // Get user by ID
        return userService.getUserById(userId);
    }

    @Override
    public ApiResponse<Void> logoutUser(String token) {
        logger.info("Logging out user");

        if (token == null || token.isEmpty()) {
            return ApiResponse.error("Authentication token is required",
                    HttpStatus.UNAUTHORIZED.value());
        }

        // Extract token from "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Remove token from store
        tokenUserMap.remove(token);

        return ApiResponse.success(null);
    }
}