package com.legalcase.user.api.controller;

import com.legalcase.commons.dto.ApiResponse;
import com.legalcase.commons.security.SecurityUtils;
import com.legalcase.user.api.dto.request.CreateUserRequest;
import com.legalcase.user.api.dto.response.UserResponse;
import com.legalcase.user.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for user management
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Create a new user
     */
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information. Requires ADMIN role.")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }
    
    /**
     * Get all users
     */
    @Operation(summary = "Get all users", description = "Retrieves all users. Requires ADMIN role.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.info("Getting all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("All users retrieved successfully", users));
    }
    
    /**
     * Get user by ID
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their ID. Requires appropriate permissions.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorizationService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    /**
     * Get current user
     */
    @Operation(summary = "Get current user", description = "Retrieves the current authenticated user.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
        
        log.info("Getting current user: {}", username);
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Current user retrieved successfully", user));
    }
    
    /**
     * Update user
     */
    @Operation(summary = "Update user", description = "Updates an existing user. Requires appropriate permissions.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorizationService.isCurrentUser(#id)")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        
        log.info("Updating user with id: {}", id);
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }
    
    /**
     * Delete user
     */
    @Operation(summary = "Delete user", description = "Deletes a user. Requires ADMIN role.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
} 