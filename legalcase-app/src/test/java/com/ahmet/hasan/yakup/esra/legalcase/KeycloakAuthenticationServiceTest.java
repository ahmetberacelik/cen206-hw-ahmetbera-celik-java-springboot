package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.repository.UserRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.KeycloakAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test class for Keycloak authentication service
 */
@ExtendWith(MockitoExtension.class)
class KeycloakAuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    private KeycloakAuthenticationService keycloakAuthService;

    @Mock
    private Keycloak keycloakMock;

    @Mock
    private RealmResource realmResourceMock;

    @Mock
    private UsersResource usersResourceMock;

    @Mock
    private RolesResource rolesResourceMock;

    @Mock
    private TokenManager tokenManagerMock;

    @Mock
    private AccessTokenResponse accessTokenResponseMock;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Mock
    private RoleRepresentation roleRepresentationMock;

    @BeforeEach
    void setUp() throws Exception {
        // Create real service instance
        keycloakAuthService = new KeycloakAuthenticationService(userRepository);

        // Set private fields
        ReflectionTestUtils.setField(keycloakAuthService, "authServerUrl", "http://localhost:8080/auth");
        ReflectionTestUtils.setField(keycloakAuthService, "realm", "test-realm");
        ReflectionTestUtils.setField(keycloakAuthService, "clientId", "test-client");
        ReflectionTestUtils.setField(keycloakAuthService, "clientSecret", "test-secret");

        // Note: We removed general stubs, each test will define its own stubs
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
    void registerUser_Success() throws Exception {
        // Arrange
        User testUser = createTestUser();

        // Create KeycloakAuthenticationService as spy
        keycloakAuthService = spy(keycloakAuthService);

        // Stub getKeycloakAdminInstance() method using doReturn
        doReturn(keycloakMock).when(keycloakAuthService).getKeycloakAdminInstance();

        // Mock Keycloak responses
        when(keycloakMock.realm(anyString())).thenReturn(realmResourceMock);
        when(realmResourceMock.users()).thenReturn(usersResourceMock);
        when(usersResourceMock.search(anyString(), anyBoolean())).thenReturn(Collections.emptyList());

        // Mock createUserWithDirectHttpCall method response
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setKeycloakId("user-123");
            user.setEnabled(true);

            User userCopy = new User();
            userCopy.setId(user.getId());
            userCopy.setUsername(user.getUsername());
            userCopy.setEmail(user.getEmail());
            userCopy.setName(user.getName());
            userCopy.setSurname(user.getSurname());
            userCopy.setRole(user.getRole());
            userCopy.setKeycloakId(user.getKeycloakId());
            userCopy.setEnabled(user.isEnabled());

            return ApiResponse.success(userCopy);
        }).when(keycloakAuthService).createUserWithDirectHttpCall(any(User.class), any(RealmResource.class));

        // Mock database responses
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertTrue(response.isSuccess());
    }

    @Test
    void registerUser_UserAlreadyExists() {
        // Arrange
        User testUser = createTestUser();
        List<UserRepresentation> existingUsers = new ArrayList<>();
        UserRepresentation existingUser = new UserRepresentation();
        existingUser.setUsername("testuser");
        existingUsers.add(existingUser);

        // Create KeycloakAuthenticationService as spy
        keycloakAuthService = spy(keycloakAuthService);

        // Stub getKeycloakAdminInstance() method using doReturn
        doReturn(keycloakMock).when(keycloakAuthService).getKeycloakAdminInstance();

        // Mock Keycloak responses
        when(keycloakMock.realm(anyString())).thenReturn(realmResourceMock);
        when(realmResourceMock.users()).thenReturn(usersResourceMock);
        when(usersResourceMock.search(eq("testuser"), anyBoolean())).thenReturn(existingUsers);

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("User already exists"));
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        User testUser = createTestUser();

        // Create KeycloakAuthenticationService as spy
        keycloakAuthService = spy(keycloakAuthService);

        // Stub getKeycloakAdminInstance() method using doReturn
        doReturn(keycloakMock).when(keycloakAuthService).getKeycloakAdminInstance();

        // Mock Keycloak responses
        when(keycloakMock.realm(anyString())).thenReturn(realmResourceMock);
        when(realmResourceMock.users()).thenReturn(usersResourceMock);
        when(usersResourceMock.search(anyString(), anyBoolean())).thenReturn(Collections.emptyList());

        // Mock database responses
        when(userRepository.findByEmail(eq("test@example.com"))).thenReturn(Optional.of(new User()));

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Email already exists"));
    }

    @Test
    void registerUser_MissingUsername() {
        // Arrange
        User testUser = createTestUser();
        testUser.setUsername("");

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
    }

    @Test
    void registerUser_MissingEmail() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEmail("");

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
    }

    @Test
    void registerUser_MissingPassword() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPassword("");

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
    }

    @Test
    void registerUser_MissingName() {
        // Arrange
        User testUser = createTestUser();
        testUser.setName("");

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
    }

    @Test
    void registerUser_MissingSurname() {
        // Arrange
        User testUser = createTestUser();
        testUser.setSurname("");

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
    }

    @Test
    void registerUser_MissingRole() {
        // Arrange
        User testUser = createTestUser();
        testUser.setRole(null);

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertFalse(response.isSuccess());
    }

    @Test
    void registerUser_KeycloakNull() {
        // Arrange
        User testUser = createTestUser();

        // Create KeycloakAuthenticationService as spy
        keycloakAuthService = spy(keycloakAuthService);

        // Stub to return null for Keycloak
        doReturn(null).when(keycloakAuthService).getKeycloakAdminInstance();

        // Mock database responses
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApiResponse<User> response = keycloakAuthService.registerUser(testUser);

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals("testuser", response.getData().getUsername());
        assertTrue(response.getData().isEnabled());

        // Verify database save operation
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.isEnabled());
        assertNull(savedUser.getKeycloakId()); // Keycloak ID should be null
    }

    @Test
    void authenticateUser_Success() {
        // Arrange
        User testUser = createTestUser();

        // Mock database responses
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(testUser));

        // Mock Keycloak builder
        try (MockedStatic<KeycloakBuilder> keycloakBuilderStatic = mockStatic(KeycloakBuilder.class)) {
            KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
            keycloakBuilderStatic.when(KeycloakBuilder::builder).thenReturn(builderMock);
            when(builderMock.serverUrl(anyString())).thenReturn(builderMock);
            when(builderMock.realm(anyString())).thenReturn(builderMock);
            when(builderMock.clientId(anyString())).thenReturn(builderMock);
            when(builderMock.clientSecret(anyString())).thenReturn(builderMock);
            when(builderMock.username(anyString())).thenReturn(builderMock);
            when(builderMock.password(anyString())).thenReturn(builderMock);
            when(builderMock.grantType(anyString())).thenReturn(builderMock);
            when(builderMock.build()).thenReturn(keycloakMock);

            // Mock token response
            when(keycloakMock.tokenManager()).thenReturn(tokenManagerMock);
            when(tokenManagerMock.getAccessToken()).thenReturn(accessTokenResponseMock);
            when(accessTokenResponseMock.getToken()).thenReturn("test-token");
            when(accessTokenResponseMock.getExpiresIn()).thenReturn(3600L);
            when(accessTokenResponseMock.getRefreshToken()).thenReturn("refresh-token");
            when(accessTokenResponseMock.getRefreshExpiresIn()).thenReturn(7200L);

            // Act
            ApiResponse<Map<String, Object>> response = keycloakAuthService.authenticateUser("testuser", "password123");

            // Assert
            assertTrue(response.isSuccess());
            assertNotNull(response.getData());
            assertEquals(testUser, response.getData().get("user"));
            assertEquals("test-token", response.getData().get("token"));
            assertEquals(3600L, response.getData().get("expiresIn"));
            assertEquals("refresh-token", response.getData().get("refreshToken"));
            assertEquals(7200L, response.getData().get("refreshExpiresIn"));
        }
    }

    @Test
    void authenticateUser_KeycloakFallback() {
        // Arrange
        User testUser = createTestUser();

        // Mock database responses
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(testUser));

        // Mock Keycloak builder
        try (MockedStatic<KeycloakBuilder> keycloakBuilderStatic = mockStatic(KeycloakBuilder.class)) {
            KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
            keycloakBuilderStatic.when(KeycloakBuilder::builder).thenReturn(builderMock);
            when(builderMock.serverUrl(anyString())).thenReturn(builderMock);
            when(builderMock.realm(anyString())).thenReturn(builderMock);
            when(builderMock.clientId(anyString())).thenReturn(builderMock);
            when(builderMock.clientSecret(anyString())).thenReturn(builderMock);
            when(builderMock.username(anyString())).thenReturn(builderMock);
            when(builderMock.password(anyString())).thenReturn(builderMock);
            when(builderMock.grantType(anyString())).thenReturn(builderMock);
            when(builderMock.build()).thenThrow(new RuntimeException("Keycloak connection error"));

            // Act
            ApiResponse<Map<String, Object>> response = keycloakAuthService.authenticateUser("testuser", "password123");

            // Assert
            assertTrue(response.isSuccess());
            assertNotNull(response.getData());
            assertEquals(testUser, response.getData().get("user"));
            assertNotNull(response.getData().get("token"));
            assertEquals(3600, response.getData().get("expiresIn"));
        }
    }

    @Test
    void authenticateUser_UserNotFound() {
        // Arrange
        // Mock database responses (no user found)
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Mock Keycloak builder
        try (MockedStatic<KeycloakBuilder> keycloakBuilderStatic = mockStatic(KeycloakBuilder.class)) {
            KeycloakBuilder builderMock = mock(KeycloakBuilder.class);
            keycloakBuilderStatic.when(KeycloakBuilder::builder).thenReturn(builderMock);
            when(builderMock.serverUrl(anyString())).thenReturn(builderMock);
            when(builderMock.realm(anyString())).thenReturn(builderMock);
            when(builderMock.clientId(anyString())).thenReturn(builderMock);
            when(builderMock.clientSecret(anyString())).thenReturn(builderMock);
            when(builderMock.username(anyString())).thenReturn(builderMock);
            when(builderMock.password(anyString())).thenReturn(builderMock);
            when(builderMock.grantType(anyString())).thenReturn(builderMock);
            when(builderMock.build()).thenThrow(new RuntimeException("Keycloak connection error"));

            // Act
            ApiResponse<Map<String, Object>> response = keycloakAuthService.authenticateUser("nonexistent", "password123");

            // Assert
            assertFalse(response.isSuccess());
            assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
            assertTrue(response.getErrorMessages().get(0).contains("User not found"));
        }
    }

    @Test
    void getCurrentUser_Success() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPassword(null);
        List<User> userList = Collections.singletonList(testUser);

        // Create KeycloakAuthenticationService as spy
        keycloakAuthService = spy(keycloakAuthService);

        // Stub getKeycloakInstance() method using doReturn (for mocking a private method for testing)
        doReturn(keycloakMock).when(keycloakAuthService).getKeycloakInstance();

        // Mock database responses
        when(userRepository.findAll()).thenReturn(userList);

        // Act
        ApiResponse<User> response = keycloakAuthService.getCurrentUser("Bearer valid-token");

        // Assert
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(testUser.getUsername(), response.getData().getUsername());
        assertNull(response.getData().getPassword());
    }

    @Test
    void getCurrentUser_EmptyToken() {
        // Act
        ApiResponse<User> response = keycloakAuthService.getCurrentUser("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Authorization token is required"));
    }

    @Test
    void getCurrentUser_NoUsersFound() {
        // Arrange
        // Create KeycloakAuthenticationService as spy
        keycloakAuthService = spy(keycloakAuthService);

        // Stub getKeycloakInstance() method using doReturn (for mocking a private method for testing)
        doReturn(keycloakMock).when(keycloakAuthService).getKeycloakInstance();

        // Mock database responses
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ApiResponse<User> response = keycloakAuthService.getCurrentUser("Bearer valid-token");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("No users found"));
    }

    @Test
    void logoutUser_Success() {
        // Act
        ApiResponse<Void> response = keycloakAuthService.logoutUser("Bearer valid-token");

        // Assert
        assertTrue(response.isSuccess());
    }

    @Test
    void logoutUser_EmptyToken() {
        // Act
        ApiResponse<Void> response = keycloakAuthService.logoutUser("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Authorization token is required"));
    }
}