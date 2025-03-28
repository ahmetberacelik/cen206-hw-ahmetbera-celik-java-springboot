package com.ahmet.hasan.yakup.esra.legalcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ahmet.hasan.yakup.esra.legalcase.api.UserAuthenticationController;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

/**
 * Test for UserAuthenticationController using Mockito
 * This isolates tests from Spring context loading issues
 */
@ExtendWith(MockitoExtension.class)
public class UserAuthenticationControllerTest {

    @Mock
    private IUserAuthenticationService authService;

    @InjectMocks
    private UserAuthenticationController authController;

    private User testUser;
    private Map<String, Object> testAuthResponse;
    private ApiResponse<User> userSuccessResponse;
    private ApiResponse<User> userErrorResponse;
    private ApiResponse<Map<String, Object>> authSuccessResponse;
    private ApiResponse<Map<String, Object>> authErrorResponse;
    private ApiResponse<Void> voidSuccessResponse;
    private String testToken;

    @BeforeEach
    public void setup() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // Setup test authentication response
        testAuthResponse = new HashMap<>();
        testAuthResponse.put("user", testUser);
        testAuthResponse.put("token", "test-jwt-token");

        // Setup test token
        testToken = "Bearer test-jwt-token";

        // Create response objects
        userSuccessResponse = ApiResponse.success(testUser);
        userErrorResponse = ApiResponse.error("User error", HttpStatus.BAD_REQUEST.value());
        authSuccessResponse = ApiResponse.success(testAuthResponse);
        authErrorResponse = ApiResponse.error("Authentication error", HttpStatus.UNAUTHORIZED.value());
        voidSuccessResponse = ApiResponse.success(null);
    }

    @Test
    public void testRegister_Success() {
        // Arrange
        when(authService.registerUser(any(User.class))).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = authController.register(testUser);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(authService).registerUser(any(User.class));
    }

    @Test
    public void testRegister_Failure() {
        // Arrange
        when(authService.registerUser(any(User.class))).thenReturn(userErrorResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = authController.register(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User error", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(authService).registerUser(any(User.class));
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("usernameOrEmail", "testuser");
        credentials.put("password", "password");

        when(authService.authenticateUser(anyString(), anyString())).thenReturn(authSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> response = authController.login(credentials);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testAuthResponse, response.getBody().getData());

        // Verify service method was called
        verify(authService).authenticateUser("testuser", "password");
    }

    @Test
    public void testLogin_MissingCredentials() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("usernameOrEmail", "testuser");
        // Password missing

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> response = authController.login(credentials);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Both usernameOrEmail and password are required", response.getBody().getErrorMessages().get(0));
    }

    @Test
    public void testLogin_Failure() {
        // Arrange
        Map<String, String> credentials = new HashMap<>();
        credentials.put("usernameOrEmail", "testuser");
        credentials.put("password", "wrong-password");

        when(authService.authenticateUser(anyString(), anyString())).thenReturn(authErrorResponse);

        // Act
        ResponseEntity<ApiResponse<Map<String, Object>>> response = authController.login(credentials);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Authentication error", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(authService).authenticateUser("testuser", "wrong-password");
    }

    @Test
    public void testGetCurrentUser_Success() {
        // Arrange
        when(authService.getCurrentUser(anyString())).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = authController.getCurrentUser(testToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(authService).getCurrentUser(testToken);
    }

    @Test
    public void testGetCurrentUser_Failure() {
        // Arrange
        when(authService.getCurrentUser(anyString())).thenReturn(userErrorResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = authController.getCurrentUser(testToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User error", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(authService).getCurrentUser(testToken);
    }

    @Test
    public void testLogout_Success() {
        // Arrange
        when(authService.logoutUser(anyString())).thenReturn(voidSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> response = authController.logout(testToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        // Verify service method was called
        verify(authService).logoutUser(testToken);
    }

    @Test
    public void testLogout_Failure() {
        // Arrange
        ApiResponse<Void> logoutErrorResponse = ApiResponse.error("Logout error", HttpStatus.BAD_REQUEST.value());
        when(authService.logoutUser(anyString())).thenReturn(logoutErrorResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> response = authController.logout(testToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Logout error", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(authService).logoutUser(testToken);
    }
}