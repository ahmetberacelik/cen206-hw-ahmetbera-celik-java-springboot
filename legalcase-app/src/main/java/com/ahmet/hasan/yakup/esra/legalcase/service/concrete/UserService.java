package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.repository.UserRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the UserService interface
 */
@Service
@Transactional
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ApiResponse<User> createUser(User user) {
        logger.info("Creating new user with username: {}", user.getUsername());

        // Validate required fields
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ApiResponse.error("Username is required", HttpStatus.BAD_REQUEST.value());
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ApiResponse.error("Email is required", HttpStatus.BAD_REQUEST.value());
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ApiResponse.error("Password is required", HttpStatus.BAD_REQUEST.value());
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            return ApiResponse.error("Name is required", HttpStatus.BAD_REQUEST.value());
        }
        if (user.getSurname() == null || user.getSurname().isEmpty()) {
            return ApiResponse.error("Surname is required", HttpStatus.BAD_REQUEST.value());
        }
        if (user.getRole() == null) {
            return ApiResponse.error("Role is required", HttpStatus.BAD_REQUEST.value());
        }

        // Check if username is already in use
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ApiResponse.error("Username is already in use", HttpStatus.CONFLICT.value());
        }

        // Check if email is already in use
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ApiResponse.error("Email is already in use", HttpStatus.CONFLICT.value());
        }

        try {
            // Encode password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Ensure user is enabled by default
            user.setEnabled(true);

            User savedUser = userRepository.save(user);

            // Clear password in returned user object for security
            savedUser.setPassword(null);

            return ApiResponse.success(savedUser);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to create user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<User> getUserById(Long id) {
        logger.info("Getting user by ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("Invalid user ID", HttpStatus.BAD_REQUEST.value());
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Clear password in returned user object for security
            user.setPassword(null);
            return ApiResponse.success(user);
        } else {
            return ApiResponse.error("User not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<User> getUserByUsername(String username) {
        logger.info("Getting user by username: {}", username);

        if (username == null || username.isEmpty()) {
            return ApiResponse.error("Username cannot be empty", HttpStatus.BAD_REQUEST.value());
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Clear password in returned user object for security
            user.setPassword(null);
            return ApiResponse.success(user);
        } else {
            return ApiResponse.error("User not found with username: " + username,
                    HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<User> getUserByEmail(String email) {
        logger.info("Getting user by email: {}", email);

        if (email == null || email.isEmpty()) {
            return ApiResponse.error("Email cannot be empty", HttpStatus.BAD_REQUEST.value());
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Clear password in returned user object for security
            user.setPassword(null);
            return ApiResponse.success(user);
        } else {
            return ApiResponse.error("User not found with email: " + email,
                    HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<User> getUserByKeycloakId(String keycloakId) {
        logger.info("Getting user by Keycloak ID: {}", keycloakId);

        if (keycloakId == null || keycloakId.isEmpty()) {
            return ApiResponse.error("Keycloak ID cannot be empty", HttpStatus.BAD_REQUEST.value());
        }

        Optional<User> userOptional = userRepository.findByKeycloakId(keycloakId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Clear password in returned user object for security
            user.setPassword(null);
            return ApiResponse.success(user);
        } else {
            return ApiResponse.error("User not found with Keycloak ID: " + keycloakId,
                    HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<User>> getAllUsers() {
        logger.info("Getting all users");

        List<User> users = userRepository.findAll();

        // Clear passwords in returned user objects for security
        users.forEach(user -> user.setPassword(null));

        return ApiResponse.success(users);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<User>> getUsersByRole(UserRole role) {
        logger.info("Getting users by role: {}", role);

        if (role == null) {
            return ApiResponse.error("Role cannot be null", HttpStatus.BAD_REQUEST.value());
        }

        List<User> users = userRepository.findByRole(role);

        // Clear passwords in returned user objects for security
        users.forEach(user -> user.setPassword(null));

        return ApiResponse.success(users);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<User>> searchUsers(String searchTerm) {
        logger.info("Searching users with term: {}", searchTerm);

        if (searchTerm == null || searchTerm.isEmpty()) {
            return ApiResponse.error("Search term cannot be empty", HttpStatus.BAD_REQUEST.value());
        }

        List<User> users = userRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
                searchTerm, searchTerm);

        // Clear passwords in returned user objects for security
        users.forEach(user -> user.setPassword(null));

        return ApiResponse.success(users);
    }

    @Override
    public ApiResponse<User> updateUser(User user) {
        logger.info("Updating user with ID: {}", user.getId());

        // Validate user ID
        if (user.getId() == null || user.getId() <= 0) {
            return ApiResponse.error("Invalid user ID", HttpStatus.BAD_REQUEST.value());
        }

        // Check if user exists
        Optional<User> existingUserOptional = userRepository.findById(user.getId());
        if (existingUserOptional.isEmpty()) {
            return ApiResponse.error("User not found with ID: " + user.getId(),
                    HttpStatus.NOT_FOUND.value());
        }

        User existingUser = existingUserOptional.get();

        try {
            // Check if username is being changed and is already in use by another user
            if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
                Optional<User> usernameCheckOptional = userRepository.findByUsername(user.getUsername());
                if (usernameCheckOptional.isPresent() && !usernameCheckOptional.get().getId().equals(user.getId())) {
                    return ApiResponse.error("Username is already in use by another user",
                            HttpStatus.CONFLICT.value());
                }
                existingUser.setUsername(user.getUsername());
            }

            // Check if email is being changed and is already in use by another user
            if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                Optional<User> emailCheckOptional = userRepository.findByEmail(user.getEmail());
                if (emailCheckOptional.isPresent() && !emailCheckOptional.get().getId().equals(user.getId())) {
                    return ApiResponse.error("Email is already in use by another user",
                            HttpStatus.CONFLICT.value());
                }
                existingUser.setEmail(user.getEmail());
            }

            // Update other fields if provided
            if (user.getName() != null) {
                existingUser.setName(user.getName());
            }

            if (user.getSurname() != null) {
                existingUser.setSurname(user.getSurname());
            }

            if (user.getRole() != null) {
                existingUser.setRole(user.getRole());
            }

            if (user.getKeycloakId() != null) {
                existingUser.setKeycloakId(user.getKeycloakId());
            }

            // Password and enabled status are handled by separate methods

            User updatedUser = userRepository.save(existingUser);

            // Clear password in returned user object for security
            updatedUser.setPassword(null);

            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to update user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Void> deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("Invalid user ID", HttpStatus.BAD_REQUEST.value());
        }

        if (!userRepository.existsById(id)) {
            return ApiResponse.error("User not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }

        try {
            userRepository.deleteById(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to delete user: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<User> changePassword(Long id, String currentPassword, String newPassword) {
        logger.info("Changing password for user with ID: {}", id);

        if (id == null || id <= 0) {
            return ApiResponse.error("Invalid user ID", HttpStatus.BAD_REQUEST.value());
        }

        if (currentPassword == null || currentPassword.isEmpty()) {
            return ApiResponse.error("Current password is required", HttpStatus.BAD_REQUEST.value());
        }

        if (newPassword == null || newPassword.isEmpty()) {
            return ApiResponse.error("New password is required", HttpStatus.BAD_REQUEST.value());
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ApiResponse.error("Current password is incorrect",
                    HttpStatus.UNAUTHORIZED.value());
        }

        try {
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            User updatedUser = userRepository.save(user);

            // Clear password in returned user object for security
            updatedUser.setPassword(null);

            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to change password: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<User> setUserEnabled(Long id, boolean enabled) {
        logger.info("Setting enabled status to {} for user with ID: {}", enabled, id);

        if (id == null || id <= 0) {
            return ApiResponse.error("Invalid user ID", HttpStatus.BAD_REQUEST.value());
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }

        User user = userOptional.get();

        try {
            user.setEnabled(enabled);
            User updatedUser = userRepository.save(user);

            // Clear password in returned user object for security
            updatedUser.setPassword(null);

            return ApiResponse.success(updatedUser);
        } catch (Exception e) {
            logger.error("Error setting user enabled status: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to update user enabled status: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}