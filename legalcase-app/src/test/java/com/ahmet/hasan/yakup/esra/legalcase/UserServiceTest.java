package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.repository.UserRepository;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
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
    void createUser_ValidUser_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertTrue(response.isSuccess());
        assertNull(response.getData().getPassword()); // Password should be cleared
        assertEquals(1L, response.getData().getId());
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_NullUsername_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setUsername(null);

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Username is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyUsername_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setUsername("");

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Username is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullEmail_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEmail(null);

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyEmail_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEmail("");

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullPassword_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPassword(null);

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Password is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyPassword_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPassword("");

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Password is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullName_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setName(null);

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Name is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptyName_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setName("");

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Name is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullSurname_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setSurname(null);

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Surname is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmptySurname_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setSurname("");

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Surname is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_NullRole_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        testUser.setRole(null);

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Role is required"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_UsernameAlreadyExists_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(new User()));

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Username is already in use"));
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(new User()));

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email is already in use"));
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_RepositoryException_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<User> response = userService.createUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to create user"));
        verify(userRepository).findByUsername(testUser.getUsername());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void getUserById_ValidId_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        ApiResponse<User> response = userService.getUserById(1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testUser.getUsername(), response.getData().getUsername());
        assertNull(response.getData().getPassword()); // Password should be cleared
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NullId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserById(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserById_ZeroId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserById(0L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserById_NegativeId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserById(-1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserById_NonExistentId_ReturnsError() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = userService.getUserById(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).findById(999L);
    }

    @Test
    void getUserByUsername_ValidUsername_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        ApiResponse<User> response = userService.getUserByUsername("testuser");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testUser.getUsername(), response.getData().getUsername());
        assertNull(response.getData().getPassword()); // Password should be cleared
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_NullUsername_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserByUsername(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Username cannot be empty"));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void getUserByUsername_EmptyUsername_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserByUsername("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Username cannot be empty"));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void getUserByUsername_NonExistentUsername_ReturnsError() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = userService.getUserByUsername("nonexistent");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void getUserByEmail_ValidEmail_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        ApiResponse<User> response = userService.getUserByEmail("test@example.com");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testUser.getEmail(), response.getData().getEmail());
        assertNull(response.getData().getPassword()); // Password should be cleared
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUserByEmail_NullEmail_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserByEmail(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email cannot be empty"));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getUserByEmail_EmptyEmail_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserByEmail("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email cannot be empty"));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void getUserByEmail_NonExistentEmail_ReturnsError() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = userService.getUserByEmail("nonexistent@example.com");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getUserByKeycloakId_ValidId_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        testUser.setKeycloakId("keycloak-id-123");
        when(userRepository.findByKeycloakId("keycloak-id-123")).thenReturn(Optional.of(testUser));

        // Act
        ApiResponse<User> response = userService.getUserByKeycloakId("keycloak-id-123");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testUser.getKeycloakId(), response.getData().getKeycloakId());
        assertNull(response.getData().getPassword()); // Password should be cleared
        verify(userRepository).findByKeycloakId("keycloak-id-123");
    }

    @Test
    void getUserByKeycloakId_NullId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserByKeycloakId(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Keycloak ID cannot be empty"));
        verify(userRepository, never()).findByKeycloakId(anyString());
    }

    @Test
    void getUserByKeycloakId_EmptyId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.getUserByKeycloakId("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Keycloak ID cannot be empty"));
        verify(userRepository, never()).findByKeycloakId(anyString());
    }

    @Test
    void getUserByKeycloakId_NonExistentId_ReturnsError() {
        // Arrange
        when(userRepository.findByKeycloakId("nonexistent-id")).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = userService.getUserByKeycloakId("nonexistent-id");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).findByKeycloakId("nonexistent-id");
    }

    @Test
    void getAllUsers_ReturnsUsersList() {
        // Arrange
        List<User> testUsers = List.of(
                createTestUser(),
                createTestUser(),
                createTestUser()
        );
        when(userRepository.findAll()).thenReturn(testUsers);

        // Act
        ApiResponse<List<User>> response = userService.getAllUsers();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().size());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<User>> response = userService.getAllUsers();

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void getUsersByRole_ValidRole_ReturnsUsersList() {
        // Arrange
        List<User> lawyerUsers = List.of(
                createTestUser(),
                createTestUser()
        );
        when(userRepository.findByRole(UserRole.LAWYER)).thenReturn(lawyerUsers);

        // Act
        ApiResponse<List<User>> response = userService.getUsersByRole(UserRole.LAWYER);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
        verify(userRepository).findByRole(UserRole.LAWYER);
    }

    @Test
    void getUsersByRole_NullRole_ReturnsError() {
        // Act
        ApiResponse<List<User>> response = userService.getUsersByRole(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Role cannot be null"));
        verify(userRepository, never()).findByRole(any(UserRole.class));
    }

    @Test
    void searchUsers_ValidTerm_ReturnsMatchingUsers() {
        // Arrange
        List<User> matchedUsers = List.of(
                createTestUser(),
                createTestUser()
        );
        when(userRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("test", "test"))
                .thenReturn(matchedUsers);

        // Act
        ApiResponse<List<User>> response = userService.searchUsers("test");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
        verify(userRepository).findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("test", "test");
    }

    @Test
    void searchUsers_NullTerm_ReturnsError() {
        // Act
        ApiResponse<List<User>> response = userService.searchUsers(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Search term cannot be empty"));
        verify(userRepository, never()).findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(anyString(), anyString());
    }

    @Test
    void searchUsers_EmptyTerm_ReturnsError() {
        // Act
        ApiResponse<List<User>> response = userService.searchUsers("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Search term cannot be empty"));
        verify(userRepository, never()).findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(anyString(), anyString());
    }

    @Test
    void updateUser_ValidUser_ReturnsUpdatedUser() {
        // Arrange
        User existingUser = createTestUser();
        User updatedUser = createTestUser();
        updatedUser.setName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApiResponse<User> response = userService.updateUser(updatedUser);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Updated Name", response.getData().getName());
        assertNull(response.getData().getPassword()); // Password should be cleared
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NullId_ReturnsError() {
        // Arrange
        User user = createTestUser();
        user.setId(null);

        // Act
        ApiResponse<User> response = userService.updateUser(user);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ZeroId_ReturnsError() {
        // Arrange
        User user = createTestUser();
        user.setId(0L);

        // Act
        ApiResponse<User> response = userService.updateUser(user);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_NonExistentUser_ReturnsError() {
        // Arrange
        User user = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = userService.updateUser(user);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_UsernameAlreadyInUse_ReturnsError() {
        // Arrange
        User existingUser = createTestUser();
        existingUser.setUsername("oldusername");

        User updatedUser = createTestUser();
        updatedUser.setUsername("newusername");

        User conflictUser = createTestUser();
        conflictUser.setId(2L);
        conflictUser.setUsername("newusername");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newusername")).thenReturn(Optional.of(conflictUser));

        // Act
        ApiResponse<User> response = userService.updateUser(updatedUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Username is already in use by another user"));
        verify(userRepository).findById(1L);
        verify(userRepository).findByUsername("newusername");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_EmailAlreadyInUse_ReturnsError() {
        // Arrange
        User existingUser = createTestUser();
        existingUser.setEmail("old@example.com");

        User updatedUser = createTestUser();
        updatedUser.setEmail("new@example.com");

        User conflictUser = createTestUser();
        conflictUser.setId(2L);
        conflictUser.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(conflictUser));

        // Act
        ApiResponse<User> response = userService.updateUser(updatedUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email is already in use by another user"));
        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_RepositoryException_ReturnsError() {
        // Arrange
        User existingUser = createTestUser();
        User updatedUser = createTestUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<User> response = userService.updateUser(updatedUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to update user"));
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ValidId_ReturnsSuccess() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = userService.deleteUser(1L);

        // Assert
        assertTrue(response.isSuccess());
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NullId_ReturnsError() {
        // Act
        ApiResponse<Void> response = userService.deleteUser(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_ZeroId_ReturnsError() {
        // Act
        ApiResponse<Void> response = userService.deleteUser(0L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).existsById(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_NonExistentId_ReturnsError() {
        // Arrange
        when(userRepository.existsById(999L)).thenReturn(false);

        // Act
        ApiResponse<Void> response = userService.deleteUser(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteUser_RepositoryException_ReturnsError() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(userRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = userService.deleteUser(1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to delete user"));
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void changePassword_ValidData_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "password123")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApiResponse<User> response = userService.changePassword(1L, "currentPassword", "newPassword");

        // Assert
        assertTrue(response.isSuccess());
        assertNull(response.getData().getPassword()); // Password should be cleared in response
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("currentPassword", "password123");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_NullId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.changePassword(null, "currentPassword", "newPassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_ZeroId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.changePassword(0L, "currentPassword", "newPassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_NullCurrentPassword_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.changePassword(1L, null, "newPassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Current password is required"));
        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_EmptyCurrentPassword_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.changePassword(1L, "", "newPassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Current password is required"));
        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_NullNewPassword_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.changePassword(1L, "currentPassword", null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("New password is required"));
        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_EmptyNewPassword_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.changePassword(1L, "currentPassword", "");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("New password is required"));
        verify(userRepository, never()).findById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_UserNotFound_ReturnsError() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = userService.changePassword(999L, "currentPassword", "newPassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).findById(999L);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_IncorrectCurrentPassword_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "password123")).thenReturn(false);

        // Act
        ApiResponse<User> response = userService.changePassword(1L, "wrongPassword", "newPassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Current password is incorrect"));
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("wrongPassword", "password123");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_RepositoryException_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("currentPassword", "password123")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<User> response = userService.changePassword(1L, "currentPassword", "newPassword");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to change password"));
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("currentPassword", "password123");
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void setUserEnabled_ValidData_ReturnsSuccess() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApiResponse<User> response = userService.setUserEnabled(1L, true);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEnabled());
        assertNull(response.getData().getPassword()); // Password should be cleared in response
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void setUserEnabled_NullId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.setUserEnabled(null, true);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserEnabled_ZeroId_ReturnsError() {
        // Act
        ApiResponse<User> response = userService.setUserEnabled(0L, true);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid user ID"));
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserEnabled_UserNotFound_ReturnsError() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = userService.setUserEnabled(999L, true);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void setUserEnabled_RepositoryException_ReturnsError() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<User> response = userService.setUserEnabled(1L, false);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to update user enabled status"));
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
}