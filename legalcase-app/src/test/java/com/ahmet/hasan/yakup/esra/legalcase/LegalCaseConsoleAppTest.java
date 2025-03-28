package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.*;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.*;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LegalCaseConsoleAppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private IUserAuthenticationService authService;
    private IUserService userService;
    private ICaseService caseService;
    private IClientService clientService;
    private IHearingService hearingService;
    private IDocumentService documentService;

    private AuthenticationConsole authConsole;
    private CaseManagementConsole caseConsole;
    private ClientManagementConsole clientConsole;
    private HearingManagementConsole hearingConsole;
    private DocumentManagementConsole documentConsole;

    private LegalCaseConsoleApp consoleApp;
    private Scanner scanner;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        // Mock all services
        authService = Mockito.mock(IUserAuthenticationService.class);
        userService = Mockito.mock(IUserService.class);
        caseService = Mockito.mock(ICaseService.class);
        clientService = Mockito.mock(IClientService.class);
        hearingService = Mockito.mock(IHearingService.class);
        documentService = Mockito.mock(IDocumentService.class);

        // Mock all consoles
        authConsole = Mockito.mock(AuthenticationConsole.class);
        caseConsole = Mockito.mock(CaseManagementConsole.class);
        clientConsole = Mockito.mock(ClientManagementConsole.class);
        hearingConsole = Mockito.mock(HearingManagementConsole.class);
        documentConsole = Mockito.mock(DocumentManagementConsole.class);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a console app with injected mocks
     */
    private LegalCaseConsoleApp createAppWithInput(String input) throws Exception {
        // Create a scanner with the provided input
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // Create a real ConsoleApp but with mocked components
        LegalCaseConsoleApp app = new LegalCaseConsoleApp(
                authService, userService, caseService,
                clientService, hearingService, documentService);

        // Replace the console components with our mocks using reflection
        replaceField(app, "authConsole", authConsole);
        replaceField(app, "caseConsole", caseConsole);
        replaceField(app, "clientConsole", clientConsole);
        replaceField(app, "hearingConsole", hearingConsole);
        replaceField(app, "documentConsole", documentConsole);

        // Replace the scanner with our test scanner
        replaceField(app, "scanner", scanner);

        return app;
    }

    private void replaceField(Object target, String fieldName, Object value) throws Exception {
        Field field = LegalCaseConsoleApp.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Helper method to set the current user in the app
     */
    private void setCurrentUser(LegalCaseConsoleApp app, User user) throws Exception {
        Field userField = LegalCaseConsoleApp.class.getDeclaredField("currentUser");
        userField.setAccessible(true);
        userField.set(app, user);
    }

    /**
     * Helper method to set the auth token in the app
     */
    private void setAuthToken(LegalCaseConsoleApp app, String token) throws Exception {
        Field tokenField = LegalCaseConsoleApp.class.getDeclaredField("authToken");
        tokenField.setAccessible(true);
        tokenField.set(app, token);
    }

    /**
     * Create a test user
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
    public void testRunLoginAndExit() throws Exception {
        // Prepare input: Login then Exit
        String input = "1\n7\n";
        consoleApp = createAppWithInput(input);

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // For login menu (options 1-3)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(3)))
                    .thenReturn(1); // Select login

            // For main menu (options 1-7)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(7); // Select exit

            // Setup login result
            User testUser = createTestUser();
            AuthenticationConsole.LoginResult loginResult =
                    new AuthenticationConsole.LoginResult(testUser, "test-token");
            when(authConsole.login()).thenReturn(loginResult);

            // Run the app
            consoleApp.run();

            // Verify
            verify(authConsole, times(1)).login();

            // Check output
            String output = outContent.toString();
            assertTrue(output.contains("Starting LegalCase Console Application"));
            assertTrue(output.contains("Closing LegalCase Console Application"));
        }
    }

    @Test
    public void testRunRegisterAndExit() throws Exception {
        // Prepare input: Register then Exit
        String input = "2\n3\n";
        consoleApp = createAppWithInput(input);

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // First return 2 (Register), then 3 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(3)))
                    .thenReturn(2, 3);

            // Run the app
            consoleApp.run();

            // Verify
            verify(authConsole, times(1)).register();

            // Check output
            String output = outContent.toString();
            assertTrue(output.contains("Starting LegalCase Console Application"));
            assertTrue(output.contains("Closing LegalCase Console Application"));
        }
    }

    @Test
    public void testMainMenuOptions() throws Exception {
        // Create app with logged-in user
        consoleApp = createAppWithInput("");
        User testUser = createTestUser();
        setCurrentUser(consoleApp, testUser);
        setAuthToken(consoleApp, "test-token");

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // Simulate selections: 1 (View Profile) then 7 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(1, 7);

            // Run the app
            consoleApp.run();

            // Verify profile view was called
            verify(authConsole, times(1)).displayUserProfile(testUser);

            // Check output
            String output = outContent.toString();
            assertTrue(output.contains("Welcome, Test User"));
        }
    }

    @Test
    public void testCaseManagement() throws Exception {
        // Create app with logged-in user
        consoleApp = createAppWithInput("");
        User testUser = createTestUser();
        setCurrentUser(consoleApp, testUser);

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // Simulate selections: 2 (Case Management) then 7 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(2, 7);

            // Run the app
            consoleApp.run();

            // Verify case console was called
            verify(caseConsole, times(1)).showMenu(testUser);
        }
    }

    @Test
    public void testClientManagement() throws Exception {
        // Create app with logged-in user
        consoleApp = createAppWithInput("");
        User testUser = createTestUser();
        setCurrentUser(consoleApp, testUser);

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // Simulate selections: 3 (Client Management) then 7 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(3, 7);

            // Run the app
            consoleApp.run();

            // Verify client console was called
            verify(clientConsole, times(1)).showMenu(testUser);
        }
    }

    @Test
    public void testHearingManagement() throws Exception {
        // Create app with logged-in user
        consoleApp = createAppWithInput("");
        User testUser = createTestUser();
        setCurrentUser(consoleApp, testUser);

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // Simulate selections: 4 (Hearing Management) then 7 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(4, 7);

            // Run the app
            consoleApp.run();

            // Verify hearing console was called
            verify(hearingConsole, times(1)).showMenu(testUser);
        }
    }

    @Test
    public void testDocumentManagement() throws Exception {
        // Create app with logged-in user
        consoleApp = createAppWithInput("");
        User testUser = createTestUser();
        setCurrentUser(consoleApp, testUser);

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // Simulate selections: 5 (Document Management) then 7 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(5, 7);

            // Run the app
            consoleApp.run();

            // Verify document console was called
            verify(documentConsole, times(1)).showMenu(testUser);
        }
    }

    @Test
    public void testLogout() throws Exception {
        // Create app with logged-in user
        consoleApp = createAppWithInput("");
        User testUser = createTestUser();
        setCurrentUser(consoleApp, testUser);
        setAuthToken(consoleApp, "test-token");

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // Simulate selections: 6 (Logout) then 3 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(6);
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(3)))
                    .thenReturn(3);

            // Run the app
            consoleApp.run();

            // Verify logout was called
            verify(authConsole, times(1)).logout("test-token");

            // Check if user was reset
            Field userField = LegalCaseConsoleApp.class.getDeclaredField("currentUser");
            userField.setAccessible(true);
            assertNull(userField.get(consoleApp), "Current user should be null after logout");

            // Check if token was reset
            Field tokenField = LegalCaseConsoleApp.class.getDeclaredField("authToken");
            tokenField.setAccessible(true);
            assertNull(tokenField.get(consoleApp), "Auth token should be null after logout");
        }
    }

    @Test
    public void testInvalidSelectionInLoginMenu() throws Exception {
        // Prepare input with invalid selection
        consoleApp = createAppWithInput("");

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // First return -1 (invalid), then 3 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(3)))
                    .thenReturn(-1, 3);

            // Run the app
            consoleApp.run();

            // Check output for invalid selection message
            String output = outContent.toString();
            assertTrue(output.contains("Invalid selection!"));
        }
    }

    @Test
    public void testInvalidSelectionInMainMenu() throws Exception {
        // Create app with logged-in user
        consoleApp = createAppWithInput("");
        User testUser = createTestUser();
        setCurrentUser(consoleApp, testUser);

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // First return -1 (invalid), then 7 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(-1, 7);

            // Run the app
            consoleApp.run();

            // Check output for invalid selection message
            String output = outContent.toString();
            assertTrue(output.contains("Invalid selection!"));
        }
    }

    @Test
    public void testCompleteWorkflow() throws Exception {
        // Create app with input
        consoleApp = createAppWithInput("");

        // Setup static ConsoleUtils.getUserChoice mock
        try (MockedStatic<ConsoleUtils> mockedStatic = Mockito.mockStatic(ConsoleUtils.class)) {
            // Simulate a complete workflow:
            // 1. Login (success)
            // 2. View Profile
            // 3. Case Management
            // 4. Client Management
            // 5. Hearing Management
            // 6. Document Management
            // 7. Logout
            // 8. Exit
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(3)))
                    .thenReturn(1, 3); // First login, then exit (after logout)

            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(7)))
                    .thenReturn(1, 2, 3, 4, 5, 6, 7); // All main menu options in sequence

            // Setup login result
            User testUser = createTestUser();
            AuthenticationConsole.LoginResult loginResult =
                    new AuthenticationConsole.LoginResult(testUser, "test-token");
            when(authConsole.login()).thenReturn(loginResult);

            // Run the app
            consoleApp.run();

            // Verify all console methods were called in the correct order
            verify(authConsole, times(1)).login();
            verify(authConsole, times(1)).displayUserProfile(testUser);
            verify(caseConsole, times(1)).showMenu(testUser);
            verify(clientConsole, times(1)).showMenu(testUser);
            verify(hearingConsole, times(1)).showMenu(testUser);
            verify(documentConsole, times(1)).showMenu(testUser);
            verify(authConsole, times(1)).logout("test-token");

            // Check output
            String output = outContent.toString();
            assertTrue(output.contains("Starting LegalCase Console Application"));
            assertTrue(output.contains("Closing LegalCase Console Application"));
        }
    }
}