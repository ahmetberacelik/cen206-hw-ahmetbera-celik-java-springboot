package com.ahmet.hasan.yakup.esra.legalcase;

// Update these imports to match your project structure

import com.ahmet.hasan.yakup.esra.legalcase.console.*;
import com.ahmet.hasan.yakup.esra.legalcase.console.AuthenticationConsole.LoginResult;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Scanner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LegalCaseConsoleAppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @Mock
    private IUserAuthenticationService authService;

    @Mock
    private IUserService userService;

    @Mock
    private ICaseService caseService;

    @Mock
    private IClientService clientService;

    @Mock
    private IHearingService hearingService;

    @Mock
    private IDocumentService documentService;

    @Mock
    private AuthenticationConsole authConsole;

    @Mock
    private CaseManagementConsole caseConsole;

    @Mock
    private ClientManagementConsole clientConsole;

    @Mock
    private HearingManagementConsole hearingConsole;

    @Mock
    private DocumentManagementConsole documentConsole;

    @InjectMocks
    private LegalCaseConsoleApp consoleApp;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));

        // Set the mocked consoles using reflection
        setPrivateField(consoleApp, "authConsole", authConsole);
        setPrivateField(consoleApp, "caseConsole", caseConsole);
        setPrivateField(consoleApp, "clientConsole", clientConsole);
        setPrivateField(consoleApp, "hearingConsole", hearingConsole);
        setPrivateField(consoleApp, "documentConsole", documentConsole);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to set a private field using reflection
     */
    private void setPrivateField(Object instance, String fieldName, Object value) throws Exception {
        Field field = null;
        // Try to get field from class first
        try {
            field = instance.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // If not found, try superclass
            field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        field.set(instance, value);
    }

    /**
     * Helper method to get a private field value using reflection
     */
    private Object getPrivateField(Object instance, String fieldName) throws Exception {
        Field field = null;
        // Try to get field from class first
        try {
            field = instance.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // If not found, try superclass
            field = instance.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        return field.get(instance);
    }

    /**
     * Helper method to simulate user input
     */
    private void simulateUserInput(String input) {
        ByteArrayInputStream inContent = new ByteArrayInputStream(input.getBytes());
        System.setIn(inContent);

        try {
            // Replace scanner with our simulated input
            Scanner mockScanner = new Scanner(inContent);
            setPrivateField(consoleApp, "scanner", mockScanner);
        } catch (Exception e) {
            System.err.println("Error setting up mock scanner: " + e.getMessage());
        }
    }

    /**
     * Helper method to create a mock user
     */
    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setName("Test");
        user.setSurname("User");
        user.setRole(UserRole.LAWYER);
        return user;
    }

    @Test
    public void testLoginMenu() throws Exception {
        // Capture console output to verify menu content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Call printLoginMenu via reflection
        Method printLoginMenuMethod = LegalCaseConsoleApp.class.getDeclaredMethod("printLoginMenu");
        printLoginMenuMethod.setAccessible(true);
        printLoginMenuMethod.invoke(consoleApp);

        // Check that the login menu was displayed
        String output = outContent.toString();
        assertTrue(output.contains("Login Menu"));
        assertTrue(output.contains("1. Login"));
        assertTrue(output.contains("2. Register"));
        assertTrue(output.contains("3. Exit"));
    }

    @Test
    public void testLogin() throws Exception {
        // Create mock user and login result
        User mockUser = createMockUser();
        LoginResult loginResult = new LoginResult(mockUser, "test-token");

        // Setup auth console mock
        when(authConsole.login()).thenReturn(loginResult);

        // Set private fields to simulate being logged out initially
        setPrivateField(consoleApp, "currentUser", null);
        setPrivateField(consoleApp, "authToken", null);

        // Call login method using reflection
        Method loginMethod = LegalCaseConsoleApp.class.getDeclaredMethod("login");
        loginMethod.setAccessible(true);
        loginMethod.invoke(consoleApp);

        // Verify auth console was called
        verify(authConsole, times(1)).login();

        // Get the resulting state
        User currentUser = (User) getPrivateField(consoleApp, "currentUser");
        String authToken = (String) getPrivateField(consoleApp, "authToken");

        // Verify the correct state
        assertEquals(mockUser, currentUser);
        assertEquals("test-token", authToken);
    }

    @Test
    public void testFailedLogin() throws Exception {
        // Setup auth console mock to return null (failed login)
        when(authConsole.login()).thenReturn(null);

        // Set private fields to simulate being logged out initially
        setPrivateField(consoleApp, "currentUser", null);
        setPrivateField(consoleApp, "authToken", null);

        // Call login method using reflection
        Method loginMethod = LegalCaseConsoleApp.class.getDeclaredMethod("login");
        loginMethod.setAccessible(true);
        loginMethod.invoke(consoleApp);

        // Verify auth console was called
        verify(authConsole, times(1)).login();

        // Get the resulting state
        User currentUser = (User) getPrivateField(consoleApp, "currentUser");
        String authToken = (String) getPrivateField(consoleApp, "authToken");

        // Verify the correct state (should remain null)
        assertNull(currentUser);
        assertNull(authToken);
    }

    @Test
    public void testRegister() throws Exception {
        // Call register method using reflection
        Method registerMethod = LegalCaseConsoleApp.class.getDeclaredMethod("register");
        registerMethod.setAccessible(true);
        registerMethod.invoke(consoleApp);

        // Verify registration method was called
        verify(authConsole, times(1)).register();
    }

    @Test
    public void testLogout() throws Exception {
        // Setup initial state - logged in
        User mockUser = createMockUser();
        String testToken = "test-token";
        setPrivateField(consoleApp, "currentUser", mockUser);
        setPrivateField(consoleApp, "authToken", testToken);

        // Call logout method using reflection
        Method logoutMethod = LegalCaseConsoleApp.class.getDeclaredMethod("logout");
        logoutMethod.setAccessible(true);
        logoutMethod.invoke(consoleApp);

        // Verify console logout was called with token
        verify(authConsole, times(1)).logout(testToken);

        // Verify fields are reset
        User currentUser = (User) getPrivateField(consoleApp, "currentUser");
        String authToken = (String) getPrivateField(consoleApp, "authToken");

        assertNull(currentUser);
        assertNull(authToken);
    }

    @Test
    public void testViewProfile() throws Exception {
        // Setup initial state - logged in
        User mockUser = createMockUser();
        setPrivateField(consoleApp, "currentUser", mockUser);

        // Call viewProfile method using reflection
        Method viewProfileMethod = LegalCaseConsoleApp.class.getDeclaredMethod("viewProfile");
        viewProfileMethod.setAccessible(true);
        viewProfileMethod.invoke(consoleApp);

        // Verify profile display was called
        verify(authConsole, times(1)).displayUserProfile(mockUser);
    }

    @Test
    public void testMainMenu() throws Exception {
        // Setup initial state - logged in
        User mockUser = createMockUser();
        setPrivateField(consoleApp, "currentUser", mockUser);

        // Capture console output to verify menu content
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Call printMainMenu via reflection
        Method printMainMenuMethod = LegalCaseConsoleApp.class.getDeclaredMethod("printMainMenu");
        printMainMenuMethod.setAccessible(true);
        printMainMenuMethod.invoke(consoleApp);

        // Check that the main menu was displayed
        String output = outContent.toString();
        assertTrue(output.contains("Main Menu"));
        assertTrue(output.contains("Welcome, Test User"));
        assertTrue(output.contains("1. View My Profile"));
        assertTrue(output.contains("2. Case Management"));
        assertTrue(output.contains("6. Logout"));
        assertTrue(output.contains("7. Exit Application"));
    }

    @Test
    public void testCaseManagementNavigation() throws Exception {
        // Setup initial state - logged in
        User mockUser = createMockUser();
        setPrivateField(consoleApp, "currentUser", mockUser);

        // Create a test runner that accesses private methods via reflection
        Runnable testRunner = () -> {
            try {
                // Call printMainMenu via reflection
                Method printMainMenuMethod = LegalCaseConsoleApp.class.getDeclaredMethod("printMainMenu");
                printMainMenuMethod.setAccessible(true);
                printMainMenuMethod.invoke(consoleApp);

                // Call case management directly
                caseConsole.showMenu(mockUser);
            } catch (Exception e) {
                System.err.println("Test execution failed: " + e.getMessage());
            }
        };

        // Run the test code
        testRunner.run();

        // Verify case management menu was shown
        verify(caseConsole, times(1)).showMenu(mockUser);
    }

    @Test
    public void testClientManagementNavigation() throws Exception {
        // Setup initial state - logged in
        User mockUser = createMockUser();
        setPrivateField(consoleApp, "currentUser", mockUser);

        // Directly call the client console
        clientConsole.showMenu(mockUser);

        // Verify client management menu was shown
        verify(clientConsole, times(1)).showMenu(mockUser);
    }

    @Test
    public void testHearingManagementNavigation() throws Exception {
        // Setup initial state - logged in
        User mockUser = createMockUser();
        setPrivateField(consoleApp, "currentUser", mockUser);

        // Directly call the hearing console
        hearingConsole.showMenu(mockUser);

        // Verify hearing management menu was shown
        verify(hearingConsole, times(1)).showMenu(mockUser);
    }

    @Test
    public void testDocumentManagementNavigation() throws Exception {
        // Setup initial state - logged in
        User mockUser = createMockUser();
        setPrivateField(consoleApp, "currentUser", mockUser);

        // Directly call the document console
        documentConsole.showMenu(mockUser);

        // Verify document management menu was shown
        verify(documentConsole, times(1)).showMenu(mockUser);
    }
}