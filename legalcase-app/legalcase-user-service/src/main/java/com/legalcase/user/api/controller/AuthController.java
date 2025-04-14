package com.legalcase.user.api.controller;

import com.legalcase.commons.dto.ApiResponse;
import com.legalcase.user.api.dto.request.CreateUserRequest;
import com.legalcase.user.api.dto.response.UserResponse;
import com.legalcase.user.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {
    
    private final UserService userService;
    
    /**
     * Register a new user
     */
    @Operation(summary = "Register a new user", description = "Registers a new user with the provided information")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
    
    // Note: This service doesn't handle token-based authentication directly
    // because it's using Keycloak to provide OAuth2/OpenID Connect.
    // The frontend will use Keycloak's endpoints for login, token refresh, etc.
} 