package com.ahmet.hasan.yakup.esra.legalcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ahmet.hasan.yakup.esra.legalcase.api.UserController;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

/**
 * Test for UserController using Mockito
 * This isolates tests from Spring context loading issues
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private List<User> testUserList;
    private ApiResponse<User> userSuccessResponse;
    private ApiResponse<User> userErrorResponse;
    private ApiResponse<List<User>> listSuccessResponse;
    private ApiResponse<Void> voidSuccessResponse;

    @BeforeEach
    public void setup() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setPassword("password");
        testUser.setRole(UserRole.LAWYER);
        testUser.setKeycloakId("keycloak-id");
        testUser.setEnabled(true);

        // Setup test user list
        testUserList = new ArrayList<>();
        testUserList.add(testUser);

        // Create response objects
        userSuccessResponse = ApiResponse.success(testUser);
        userErrorResponse = ApiResponse.error("User error", HttpStatus.BAD_REQUEST.value());
        listSuccessResponse = ApiResponse.success(testUserList);
        voidSuccessResponse = ApiResponse.success(null);
    }

    @Test
    public void testCreateUser_Success() {
        // Arrange
        when(userService.createUser(any(User.class))).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.createUser(testUser);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).createUser(any(User.class));
    }

    @Test
    public void testCreateUser_Failure() {
        // Arrange
        when(userService.createUser(any(User.class))).thenReturn(userErrorResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.createUser(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User error", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(userService).createUser(any(User.class));
    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<User>>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUserList, response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());

        // Verify service method was called
        verify(userService).getAllUsers();
    }

    @Test
    public void testGetUserById_Success() {
        // Arrange
        when(userService.getUserById(anyLong())).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).getUserById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        // Arrange
        when(userService.getUserById(anyLong())).thenReturn(
                ApiResponse.error("User not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(userService).getUserById(1L);
    }

    @Test
    public void testGetUserByUsername_Success() {
        // Arrange
        when(userService.getUserByUsername(anyString())).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.getUserByUsername("testuser");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).getUserByUsername("testuser");
    }

    @Test
    public void testGetUserByEmail_Success() {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.getUserByEmail("test@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    public void testGetUserByKeycloakId_Success() {
        // Arrange
        when(userService.getUserByKeycloakId(anyString())).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.getUserByKeycloakId("keycloak-id");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).getUserByKeycloakId("keycloak-id");
    }

    @Test
    public void testGetUsersByRole() {
        // Arrange
        when(userService.getUsersByRole(any(UserRole.class))).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<User>>> response = userController.getUsersByRole(UserRole.LAWYER);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUserList, response.getBody().getData());

        // Verify service method was called
        verify(userService).getUsersByRole(UserRole.LAWYER);
    }

    @Test
    public void testSearchUsers() {
        // Arrange
        when(userService.searchUsers(anyString())).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<User>>> response = userController.searchUsers("Test");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUserList, response.getBody().getData());

        // Verify service method was called
        verify(userService).searchUsers("Test");
    }

    @Test
    public void testUpdateUser_Success() {
        // Arrange
        when(userService.updateUser(any(User.class))).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(1L, testUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).updateUser(testUser);
    }

    @Test
    public void testUpdateUser_SetIdWhenNull() {
        // Arrange
        User userWithoutId = new User();
        userWithoutId.setUsername("testuser");
        userWithoutId.setEmail("test@example.com");

        when(userService.updateUser(any(User.class))).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(1L, userWithoutId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1L, userWithoutId.getId());

        // Verify service method was called
        verify(userService).updateUser(userWithoutId);
    }

    @Test
    public void testUpdateUser_IdMismatch() {
        // Arrange
        User userWithDifferentId = new User();
        userWithDifferentId.setId(2L);
        userWithDifferentId.setUsername("testuser");

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.updateUser(1L, userWithDifferentId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("ID in the URL does not match the ID in the request body", response.getBody().getErrorMessages().get(0));
    }

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        when(userService.deleteUser(anyLong())).thenReturn(voidSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<Void>> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        // Verify service method was called
        verify(userService).deleteUser(1L);
    }

    @Test
    public void testChangePassword_Success() {
        // Arrange
        Map<String, String> passwords = new HashMap<>();
        passwords.put("currentPassword", "oldpassword");
        passwords.put("newPassword", "newpassword");

        when(userService.changePassword(anyLong(), anyString(), anyString())).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.changePassword(1L, passwords);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).changePassword(1L, "oldpassword", "newpassword");
    }

    @Test
    public void testChangePassword_MissingPassword() {
        // Arrange
        Map<String, String> passwords = new HashMap<>();
        passwords.put("currentPassword", "oldpassword");
        // new password missing

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.changePassword(1L, passwords);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Both currentPassword and newPassword are required", response.getBody().getErrorMessages().get(0));
    }

    @Test
    public void testSetUserEnabled_Success() {
        // Arrange
        when(userService.setUserEnabled(anyLong(), eq(true))).thenReturn(userSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.setUserEnabled(1L, true);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testUser, response.getBody().getData());

        // Verify service method was called
        verify(userService).setUserEnabled(1L, true);
    }

    @Test
    public void testSetUserEnabled_Disable() {
        // Arrange
        User disabledUser = new User();
        disabledUser.setId(1L);
        disabledUser.setUsername("testuser");
        disabledUser.setEnabled(false);

        ApiResponse<User> disabledResponse = ApiResponse.success(disabledUser);

        when(userService.setUserEnabled(anyLong(), eq(false))).thenReturn(disabledResponse);

        // Act
        ResponseEntity<ApiResponse<User>> response = userController.setUserEnabled(1L, false);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(disabledUser, response.getBody().getData());
        assertFalse(response.getBody().getData().isEnabled());

        // Verify service method was called
        verify(userService).setUserEnabled(1L, false);
    }
}