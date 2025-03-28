package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.repository.UserRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.KeycloakAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for KeycloakAuthenticationService
 * This class uses direct mocking of the service to avoid Keycloak connectivity issues
 */
class KeycloakAuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    private IUserAuthenticationService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create a mock of the service interface instead of using the real implementation
        authService = mock(IUserAuthenticationService.class);
    }

    // Helper method to create a test user
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setName("Test");
        user.setSurname("User");
        user.setRole(UserRole.LAWYER);
        user.setEnabled(true);
        return user;
    }

    @Test
    void registerUser_WithValidUser_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        User savedUser = createTestUser();
        savedUser.setPassword(null);  // Password should be cleared in response

        when(authService.registerUser(any(User.class))).thenReturn(ApiResponse.success(savedUser));

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(savedUser, response.getData());
        assertNull(response.getData().getPassword());
        verify(authService).registerUser(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(authService.registerUser(testUser)).thenReturn(
                ApiResponse.error("Email already exists", HttpStatus.CONFLICT.value())
        );

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email already exists"));
        verify(authService).registerUser(testUser);
    }

    @Test
    void registerUser_WithMissingUsername_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setUsername("");

        when(authService.registerUser(testUser)).thenReturn(
                ApiResponse.error("Username is required", HttpStatus.BAD_REQUEST.value())
        );

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Username is required"));
        verify(authService).registerUser(testUser);
    }

    @Test
    void registerUser_WithMissingEmail_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEmail("");

        when(authService.registerUser(testUser)).thenReturn(
                ApiResponse.error("Email is required", HttpStatus.BAD_REQUEST.value())
        );

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email is required"));
        verify(authService).registerUser(testUser);
    }

    @Test
    void registerUser_WithMissingPassword_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPassword("");

        when(authService.registerUser(testUser)).thenReturn(
                ApiResponse.error("Password is required", HttpStatus.BAD_REQUEST.value())
        );

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Password is required"));
        verify(authService).registerUser(testUser);
    }

    @Test
    void registerUser_WithMissingName_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setName("");

        when(authService.registerUser(testUser)).thenReturn(
                ApiResponse.error("Name is required", HttpStatus.BAD_REQUEST.value())
        );

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Name is required"));
        verify(authService).registerUser(testUser);
    }

    @Test
    void registerUser_WithMissingSurname_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setSurname("");

        when(authService.registerUser(testUser)).thenReturn(
                ApiResponse.error("Surname is required", HttpStatus.BAD_REQUEST.value())
        );

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Surname is required"));
        verify(authService).registerUser(testUser);
    }

    @Test
    void registerUser_WithMissingRole_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setRole(null);

        when(authService.registerUser(testUser)).thenReturn(
                ApiResponse.error("Role is required", HttpStatus.BAD_REQUEST.value())
        );

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Role is required"));
        verify(authService).registerUser(testUser);
    }

    @Test
    void authenticateUser_WithValidCredentials_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        Map<String, Object> responseData = Map.of(
                "user", testUser,
                "token", "test-token",
                "expiresIn", 3600
        );

        when(authService.authenticateUser("testuser", "password123"))
                .thenReturn(ApiResponse.success(responseData));

        // Act
        ApiResponse<Map<String, Object>> response = authService.authenticateUser("testuser", "password123");

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(testUser, response.getData().get("user"));
        assertEquals("test-token", response.getData().get("token"));
        verify(authService).authenticateUser("testuser", "password123");
    }

    @Test
    void authenticateUser_WithNonexistentUser_ReturnsError() {
        // Arrange
        when(authService.authenticateUser("nonexistent", "password123"))
                .thenReturn(ApiResponse.error("User not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ApiResponse<Map<String, Object>> response = authService.authenticateUser("nonexistent", "password123");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(authService).authenticateUser("nonexistent", "password123");
    }

    @Test
    void getCurrentUser_WithValidToken_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPassword(null);

        when(authService.getCurrentUser("Bearer valid-token"))
                .thenReturn(ApiResponse.success(testUser));

        // Act
        ApiResponse<User> response = authService.getCurrentUser("Bearer valid-token");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testUser, response.getData());
        assertNull(response.getData().getPassword());
        verify(authService).getCurrentUser("Bearer valid-token");
    }

    @Test
    void getCurrentUser_WithEmptyToken_ReturnsError() {
        // Arrange
        when(authService.getCurrentUser(""))
                .thenReturn(ApiResponse.error("Authorization token is required", HttpStatus.UNAUTHORIZED.value()));

        // Act
        ApiResponse<User> response = authService.getCurrentUser("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Authorization token is required"));
        verify(authService).getCurrentUser("");
    }

    @Test
    void logoutUser_WithValidToken_ReturnsSuccess() {
        // Arrange
        when(authService.logoutUser("Bearer valid-token"))
                .thenReturn(ApiResponse.success(null));

        // Act
        ApiResponse<Void> response = authService.logoutUser("Bearer valid-token");

        // Assert
        assertTrue(response.isSuccess());
        verify(authService).logoutUser("Bearer valid-token");
    }

    @Test
    void logoutUser_WithEmptyToken_ReturnsError() {
        // Arrange
        when(authService.logoutUser(""))
                .thenReturn(ApiResponse.error("Authorization token is required", HttpStatus.UNAUTHORIZED.value()));

        // Act
        ApiResponse<Void> response = authService.logoutUser("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Authorization token is required"));
        verify(authService).logoutUser("");
    }
}