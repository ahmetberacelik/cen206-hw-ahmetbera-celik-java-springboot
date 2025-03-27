package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.AuthenticationConsole;
import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AuthenticationConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private IUserAuthenticationService authService;
    private IUserService userService;
    private ConsoleUtils utils;
    private Scanner testScanner;
    private Logger mockLogger;
    private AuthenticationConsole authenticationConsole;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        // Setup mocks
        authService = mock(IUserAuthenticationService.class);
        userService = mock(IUserService.class);
        mockLogger = mock(Logger.class);

        // Mock Scanner
        testScanner = mock(Scanner.class);

        // Setup utils mock with proper return behaviors
        utils = mock(ConsoleUtils.class);
        when(utils.getScanner()).thenReturn(testScanner);
        when(utils.getLogger()).thenReturn(mockLogger);
        doNothing().when(utils).waitForEnter();

        authenticationConsole = new AuthenticationConsole(authService, userService, utils);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a console with simulated input
     */
    private AuthenticationConsole createConsoleWithInput(String input) {
        Scanner scanner = new Scanner(new java.io.ByteArrayInputStream(input.getBytes()));
        ConsoleUtils consoleUtils = mock(ConsoleUtils.class);
        when(consoleUtils.getScanner()).thenReturn(scanner);
        when(consoleUtils.getLogger()).thenReturn(mockLogger);
        doNothing().when(consoleUtils).waitForEnter();

        return new AuthenticationConsole(authService, userService, consoleUtils);
    }

    /**
     * Helper method to create a test user
     */
    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setName("Test");
        user.setSurname("User");
        user.setRole(UserRole.CLIENT);
        user.setEnabled(true);
        return user;
    }

    @Test
    public void testSuccessfulLogin() {
        // Setup input
        authenticationConsole = createConsoleWithInput("testuser\npassword\n");

        // Setup mock responses
        User testUser = createTestUser();
        Map<String, Object> loginData = new HashMap<>();
        loginData.put("user", testUser);
        loginData.put("token", "test-token");
        when(authService.authenticateUser("testuser", "password"))
                .thenReturn(ApiResponse.success(loginData));

        // Execute method
        AuthenticationConsole.LoginResult result = authenticationConsole.login();

        // Verify service call
        verify(authService).authenticateUser("testuser", "password");

        // Assertions
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertEquals("test-token", result.getToken());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Login successful"));
    }

    @Test
    public void testFailedLogin() {
        // Setup input
        authenticationConsole = createConsoleWithInput("testuser\nwrongpassword\n");

        // Setup mock responses
        when(authService.authenticateUser("testuser", "wrongpassword"))
                .thenReturn(ApiResponse.error("Invalid credentials", 401));

        // Execute method
        AuthenticationConsole.LoginResult result = authenticationConsole.login();

        // Verify service call
        verify(authService).authenticateUser("testuser", "wrongpassword");

        // Assertions
        assertNull(result);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Login failed"));
    }

    @Test
    public void testSuccessfulRegistration() {
        // Setup input sequence: username, email, name, surname, password, role
        String input = "newuser\nnewuser@example.com\nNew\nUser\npassword\n5\n";
        authenticationConsole = createConsoleWithInput(input);

        // Mocking getUserChoice for role selection
        try (var mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(), eq(5))).thenReturn(5);

            // Setup mock responses
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setEmail("newuser@example.com");
            newUser.setName("New");
            newUser.setSurname("User");
            newUser.setRole(UserRole.CLIENT);
            when(authService.registerUser(any(User.class)))
                    .thenReturn(ApiResponse.success(newUser));

            // Execute method
            authenticationConsole.register();
        }

        // Verify service call
        verify(authService).registerUser(any(User.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Registration successful"));
    }

    @Test
    public void testFailedRegistration() {
        // Setup input sequence: username, email, name, surname, password, role
        String input = "newuser\nnewuser@example.com\nNew\nUser\npassword\n5\n";
        authenticationConsole = createConsoleWithInput(input);

        // Mocking getUserChoice for role selection
        try (var mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(), eq(5))).thenReturn(5);

            // Setup mock responses
            when(authService.registerUser(any(User.class)))
                    .thenReturn(ApiResponse.error("Username already exists", 400));

            // Execute method
            authenticationConsole.register();
        }

        // Verify service call
        verify(authService).registerUser(any(User.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Registration failed"));
    }

    @Test
    public void testSuccessfulLogout() {
        // Setup mock response
        when(authService.logoutUser("test-token"))
                .thenReturn(ApiResponse.success(null));

        // Execute method
        authenticationConsole.logout("test-token");

        // Verify service call
        verify(authService).logoutUser("test-token");

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Successfully logged out"));
    }

    @Test
    public void testFailedLogout() {
        // Setup mock response
        when(authService.logoutUser("test-token"))
                .thenReturn(ApiResponse.error("Logout failed", 500));

        // Execute method
        authenticationConsole.logout("test-token");

        // Verify service call
        verify(authService).logoutUser("test-token");

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("An issue occurred during logout"));
    }

    @Test
    public void testDisplayUserProfile() {
        // Setup test user
        User testUser = createTestUser();

        // Execute method
        authenticationConsole.displayUserProfile(testUser);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("User Profile"));
        assertTrue(output.contains("Username: " + testUser.getUsername()));
        assertTrue(output.contains("Email: " + testUser.getEmail()));
    }

    @Test
    public void testListUsers() {
        // Setup test users
        List<User> users = new ArrayList<>();
        users.add(createTestUser());
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("anotheruser");
        user2.setEmail("another@example.com");
        user2.setName("Another");
        user2.setSurname("User");
        user2.setRole(UserRole.LAWYER);
        users.add(user2);

        // Setup mock response
        when(userService.getAllUsers())
                .thenReturn(ApiResponse.success(users));

        // Execute method
        authenticationConsole.listUsers();

        // Verify service call
        verify(userService).getAllUsers();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("User List"));
        assertTrue(output.contains("testuser"));
        assertTrue(output.contains("anotheruser"));
    }

    @Test
    public void testListUsersEmpty() {
        // Setup mock response
        when(userService.getAllUsers())
                .thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute method
        authenticationConsole.listUsers();

        // Verify service call
        verify(userService).getAllUsers();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("No users found"));
    }

    @Test
    public void testListUsersError() {
        // Setup mock response
        when(userService.getAllUsers())
                .thenReturn(ApiResponse.error("Database error", 500));

        // Execute method
        authenticationConsole.listUsers();

        // Verify service call
        verify(userService).getAllUsers();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Could not retrieve users"));
    }
}