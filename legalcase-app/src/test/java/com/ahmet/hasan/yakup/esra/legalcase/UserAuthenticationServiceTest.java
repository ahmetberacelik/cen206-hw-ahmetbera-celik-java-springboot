package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.repository.UserRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.UserAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserAuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IUserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserAuthenticationService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new UserAuthenticationService(userRepository, userService, passwordEncoder);
    }

    // Helper method to create a test user
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword123");
        user.setName("Test");
        user.setSurname("User");
        user.setRole(UserRole.LAWYER);
        user.setEnabled(true);
        return user;
    }

    @Test
    void registerUser_CallsUserService() {
        // Arrange
        User testUser = createTestUser();
        when(userService.createUser(any(User.class))).thenReturn(ApiResponse.success(testUser));

        // Act
        ApiResponse<User> response = authService.registerUser(testUser);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testUser, response.getData());
        verify(userService).createUser(testUser);
    }

    @Test
    void authenticateUser_WithValidUsername_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword123")).thenReturn(true);

        // Act
        ApiResponse<Map<String, Object>> response = authService.authenticateUser("testuser", "password123");

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        User responseUser = (User) response.getData().get("user");
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        assertNotNull(response.getData().get("token"));
        assertNull(responseUser.getPassword()); // Password should be cleared
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "hashedPassword123");
    }

    @Test
    void authenticateUser_WithValidEmail_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword123")).thenReturn(true);

        // Act
        ApiResponse<Map<String, Object>> response = authService.authenticateUser("test@example.com", "password123");

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        User responseUser = (User) response.getData().get("user");
        assertEquals(testUser.getUsername(), responseUser.getUsername());
        assertNotNull(response.getData().get("token"));
        verify(userRepository).findByUsername("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword123");
    }

    @Test
    void authenticateUser_WithNonexistentUser_ReturnsError() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("nonexistent")).thenReturn(Optional.empty());

        // Act
        ApiResponse<Map<String, Object>> response = authService.authenticateUser("nonexistent", "password123");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid username/email or password"));
        verify(userRepository).findByUsername("nonexistent");
        verify(userRepository).findByEmail("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticateUser_WithDisabledAccount_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        ApiResponse<Map<String, Object>> response = authService.authenticateUser("testuser", "password123");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Account is disabled"));
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void authenticateUser_WithIncorrectPassword_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword123")).thenReturn(false);

        // Act
        ApiResponse<Map<String, Object>> response = authService.authenticateUser("testuser", "wrongpassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid username/email or password"));
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("wrongpassword", "hashedPassword123");
    }

    @Test
    void getCurrentUser_WithValidToken_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        // Use reflection to access the private token map
        Map<String, Long> tokenUserMap = new HashMap<>();
        try {
            java.lang.reflect.Field field = UserAuthenticationService.class.getDeclaredField("tokenUserMap");
            field.setAccessible(true);
            tokenUserMap = (Map<String, Long>) field.get(null);
        } catch (Exception e) {
            fail("Failed to access private field: " + e.getMessage());
        }

        // Add a token to the map
        tokenUserMap.put("validToken", 1L);

        when(userService.getUserById(1L)).thenReturn(ApiResponse.success(testUser));

        // Act
        ApiResponse<User> response = authService.getCurrentUser("Bearer validToken");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testUser, response.getData());
        verify(userService).getUserById(1L);
    }

    @Test
    void getCurrentUser_WithEmptyToken_ReturnsError() {
        // Act
        ApiResponse<User> response = authService.getCurrentUser("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Authentication token is required"));
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    void getCurrentUser_WithInvalidToken_ReturnsError() {
        // Act
        ApiResponse<User> response = authService.getCurrentUser("Bearer invalidToken");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid or expired token"));
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    void logoutUser_WithValidToken_ReturnsSuccess() {
        // Arrange
        // Use reflection to access the private token map
        Map<String, Long> tokenUserMap = new HashMap<>();
        try {
            java.lang.reflect.Field field = UserAuthenticationService.class.getDeclaredField("tokenUserMap");
            field.setAccessible(true);
            tokenUserMap = (Map<String, Long>) field.get(null);
        } catch (Exception e) {
            fail("Failed to access private field: " + e.getMessage());
        }

        // Add a token to the map
        tokenUserMap.put("validToken", 1L);

        // Act
        ApiResponse<Void> response = authService.logoutUser("Bearer validToken");

        // Assert
        assertTrue(response.isSuccess());
        assertFalse(tokenUserMap.containsKey("validToken")); // Token should be removed
    }

    @Test
    void logoutUser_WithEmptyToken_ReturnsError() {
        // Act
        ApiResponse<Void> response = authService.logoutUser("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Authentication token is required"));
    }
}