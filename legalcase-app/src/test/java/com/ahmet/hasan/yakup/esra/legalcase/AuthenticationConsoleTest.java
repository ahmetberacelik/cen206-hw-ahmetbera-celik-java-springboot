package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.AuthenticationConsole;
import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private IUserAuthenticationService authService;
    private IUserService userService;
    private ConsoleUtils utils;
    private Logger mockLogger;
    private AuthenticationConsole authConsole;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        authService = Mockito.mock(IUserAuthenticationService.class);
        userService = Mockito.mock(IUserService.class);
        mockLogger = Mockito.mock(Logger.class);
        utils = Mockito.mock(ConsoleUtils.class);
        when(utils.getLogger()).thenReturn(mockLogger);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a console instance with simulated user input
     */
    private AuthenticationConsole createConsoleWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(utils.getScanner()).thenReturn(scanner);
        return new AuthenticationConsole(authService, userService, utils);
    }

    /**
     * Helper method to create a test user
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setName("Test");
        user.setSurname("User");
        user.setRole(UserRole.LAWYER);
        user.setEnabled(true);
        return user;
    }

    @Test
    public void testLoginSuccess() {
        // Setup input
        authConsole = createConsoleWithInput("testuser\npassword123\n");

        // Create test user and response data
        User testUser = createTestUser();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("user", testUser);
        responseData.put("token", "test-auth-token");

        // Mock service response for successful login
        when(authService.authenticateUser("testuser", "password123"))
                .thenReturn(ApiResponse.success(responseData));

        // Execute method
        AuthenticationConsole.LoginResult result = authConsole.login();

        // Verify
        assertNotNull(result, "Should return a non-null result for successful login");
        assertEquals(testUser, result.getUser(), "Should return the user from the API response");
        assertEquals("test-auth-token", result.getToken(), "Should return the token from the API response");

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Login successful"));
    }

    @Test
    public void testLoginFailure() {
        // Setup input
        authConsole = createConsoleWithInput("wronguser\nwrongpass\n");

        // Mock service response for failed login
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("Invalid credentials");
        when(authService.authenticateUser("wronguser", "wrongpass"))
                .thenReturn(ApiResponse.error(errorMessages, 401));

        // Execute method
        AuthenticationConsole.LoginResult result = authConsole.login();

        // Verify
        assertNull(result, "Should return null for failed login");
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Login failed: Invalid credentials"));
    }

    @Test
    public void testLoginException() {
        // Setup input
        authConsole = createConsoleWithInput("testuser\npassword123\n");

        // Mock service response that throws exception
        when(authService.authenticateUser("testuser", "password123"))
                .thenThrow(new RuntimeException("Connection error"));

        // Execute method
        AuthenticationConsole.LoginResult result = authConsole.login();

        // Verify
        assertNull(result, "Should return null when exception occurs");
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("An error occurred during login: Connection error"));
    }

    @Test
    public void testRegisterSuccess() {
        // Setup input for registration with LAWYER role
        authConsole = createConsoleWithInput("newuser\nuser@example.com\nJohn\nDoe\npassword123\n2\n");

        // Setup static ConsoleUtils.getUserChoice mock
        try (var mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5)))
                    .thenReturn(2); // LAWYER role

            // Mock service response for successful registration
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setEmail("user@example.com");
            newUser.setName("John");
            newUser.setSurname("Doe");
            newUser.setPassword("password123");
            newUser.setRole(UserRole.LAWYER);
            newUser.setEnabled(true);

            // We need to use an ArgumentCaptor to capture the user object passed to registerUser
            when(authService.registerUser(any(User.class)))
                    .thenReturn(ApiResponse.success(newUser));

            // Execute method
            authConsole.register();

            // Verify service called with correct parameters
            verify(authService).registerUser(argThat(user ->
                    "newuser".equals(user.getUsername()) &&
                            "user@example.com".equals(user.getEmail()) &&
                            "John".equals(user.getName()) &&
                            "Doe".equals(user.getSurname()) &&
                            "password123".equals(user.getPassword()) &&
                            UserRole.LAWYER.equals(user.getRole()) &&
                            user.isEnabled()
            ));

            verify(utils).waitForEnter();

            // Check output
            String output = outContent.toString();
            assertTrue(output.contains("Registration successful"));
        }
    }

    @Test
    public void testRegisterFailure() {
        // Setup input for registration
        authConsole = createConsoleWithInput("existinguser\nuser@example.com\nJohn\nDoe\npassword123\n3\n");

        // Setup static ConsoleUtils.getUserChoice mock
        try (var mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5)))
                    .thenReturn(3); // ASSISTANT role

            // Mock service response for failed registration
            List<String> errorMessages = new ArrayList<>();
            errorMessages.add("Username already exists");
            when(authService.registerUser(any(User.class)))
                    .thenReturn(ApiResponse.error(errorMessages, 400));

            // Execute method
            authConsole.register();

            // Verify
            verify(utils).waitForEnter();

            // Check output
            String output = outContent.toString();
            assertTrue(output.contains("Registration failed: Username already exists"));
        }
    }

    @Test
    public void testLogoutSuccess() {
        // Setup
        authConsole = createConsoleWithInput("");

        // Mock service response
        when(authService.logoutUser("test-token"))
                .thenReturn(ApiResponse.success(null));

        // Execute method
        authConsole.logout("test-token");

        // Verify
        verify(authService).logoutUser("test-token");

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Successfully logged out"));
    }

    @Test
    public void testLogoutFailure() {
        // Setup
        authConsole = createConsoleWithInput("");

        // Mock service response
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("Invalid token");
        when(authService.logoutUser("invalid-token"))
                .thenReturn(ApiResponse.error(errorMessages, 401));

        // Execute method
        authConsole.logout("invalid-token");

        // Verify
        verify(authService).logoutUser("invalid-token");

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("An issue occurred during logout: Invalid token"));
    }

    @Test
    public void testDisplayUserProfile() {
        // Setup
        authConsole = createConsoleWithInput("");
        User testUser = createTestUser();

        // Execute method
        authConsole.displayUserProfile(testUser);

        // Verify
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("User Profile"));
        assertTrue(output.contains("User ID: 1"));
        assertTrue(output.contains("Username: testuser"));
        assertTrue(output.contains("Email: test@example.com"));
        assertTrue(output.contains("First Name: Test"));
        assertTrue(output.contains("Last Name: User"));
        assertTrue(output.contains("Role: " + UserRole.LAWYER));
        assertTrue(output.contains("Account Active: Yes"));
    }

    @Test
    public void testListUsersSuccess() {
        // Setup
        authConsole = createConsoleWithInput("");
        List<User> userList = new ArrayList<>();
        userList.add(createTestUser());

        // Add a second user
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("admin");
        user2.setEmail("admin@example.com");
        user2.setName("Admin");
        user2.setSurname("User");
        user2.setRole(UserRole.ADMIN);
        user2.setEnabled(true);
        userList.add(user2);

        // Mock service response
        when(userService.getAllUsers())
                .thenReturn(ApiResponse.success(userList));

        // Execute method
        authConsole.listUsers();

        // Verify
        verify(userService).getAllUsers();
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("User List"));
        assertTrue(output.contains("testuser"));
        assertTrue(output.contains("admin"));
    }

    @Test
    public void testListUsersEmpty() {
        // Setup
        authConsole = createConsoleWithInput("");
        List<User> emptyList = new ArrayList<>();

        // Mock service response
        when(userService.getAllUsers())
                .thenReturn(ApiResponse.success(emptyList));

        // Execute method
        authConsole.listUsers();

        // Verify
        verify(userService).getAllUsers();
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("No users found"));
    }

    @Test
    public void testListUsersError() {
        // Setup
        authConsole = createConsoleWithInput("");
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add("Database connection error");

        // Mock service response
        when(userService.getAllUsers())
                .thenReturn(ApiResponse.error(errorMessages, 500));

        // Execute method
        authConsole.listUsers();

        // Verify
        verify(userService).getAllUsers();
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Could not retrieve users: Database connection error"));
    }
}