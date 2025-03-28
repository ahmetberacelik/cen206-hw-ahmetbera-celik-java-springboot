package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.console.CaseManagementConsole;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CaseManagementConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private ICaseService caseService;
    private IClientService clientService;
    private ConsoleUtils utils;
    private Scanner testScanner;
    private Logger mockLogger;
    private CaseManagementConsole caseManagementConsole;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        // Setup mocks
        caseService = mock(ICaseService.class);
        clientService = mock(IClientService.class);
        mockLogger = mock(Logger.class);

        // Mock Scanner
        testScanner = mock(Scanner.class);

        // Setup utils mock with proper return behaviors
        utils = mock(ConsoleUtils.class);
        when(utils.getScanner()).thenReturn(testScanner);
        when(utils.getLogger()).thenReturn(mockLogger);
        doNothing().when(utils).waitForEnter();

        // For truncateString calls
        when(utils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            if (str == null) {
                return "N/A";
            }
            if (str.length() <= maxLength) {
                return str;
            }
            return str.substring(0, maxLength - 3) + "...";
        });

        caseManagementConsole = new CaseManagementConsole(caseService, clientService, utils);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a console with simulated input
     */
    private CaseManagementConsole createConsoleWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ConsoleUtils consoleUtils = mock(ConsoleUtils.class);
        when(consoleUtils.getScanner()).thenReturn(scanner);
        when(consoleUtils.getLogger()).thenReturn(mockLogger);
        doNothing().when(consoleUtils).waitForEnter();

        // For truncateString calls
        when(consoleUtils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            if (str == null) {
                return "N/A";
            }
            if (str.length() <= maxLength) {
                return str;
            }
            return str.substring(0, maxLength - 3) + "...";
        });

        return new CaseManagementConsole(caseService, clientService, consoleUtils);
    }

    /**
     * Helper method to create test case data
     */
    private List<Case> createTestCases() {
        List<Case> cases = new ArrayList<>();

        Case case1 = new Case(1L, "C-001", "Test Civil Case", CaseType.CIVIL);
        case1.setStatus(CaseStatus.ACTIVE);
        case1.setDescription("A test civil case description");

        Case case2 = new Case(2L, "C-002", "Test Criminal Case", CaseType.CRIMINAL);
        case2.setStatus(CaseStatus.NEW);
        case2.setDescription("A test criminal case description");

        Case case3 = new Case(3L, "C-003", "Test Family Case", CaseType.FAMILY);
        case3.setStatus(CaseStatus.PENDING);
        case3.setDescription("A test family case description");

        cases.add(case1);
        cases.add(case2);
        cases.add(case3);

        return cases;
    }

    /**
     * Helper method to create test client data
     */
    private List<Client> createTestClients() {
        List<Client> clients = new ArrayList<>();
        clients.add(new Client(1L, "John", "Doe", "john.doe@example.com"));
        clients.add(new Client(2L, "Jane", "Smith", "jane.smith@example.com"));
        clients.add(new Client(3L, "Michael", "Johnson", "michael.johnson@example.com"));
        return clients;
    }

    @Test
    public void testViewAllCases() {
        // Setup mock response
        List<Case> testCases = createTestCases();
        when(caseService.getAllCases()).thenReturn(ApiResponse.success(testCases));

        // Execute method
        caseManagementConsole.viewAllCases();

        // Verify service call
        verify(caseService).getAllCases();
        verify(utils).waitForEnter();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("All Cases"));
        assertTrue(output.contains("C-001"));
        assertTrue(output.contains("Test Civil Case"));
        assertTrue(output.contains("Total cases: " + testCases.size()));
    }

    @Test
    public void testViewAllCasesEmpty() {
        // Setup mock empty response
        when(caseService.getAllCases()).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute method
        caseManagementConsole.viewAllCases();

        // Verify service call
        verify(caseService).getAllCases();
        verify(utils).waitForEnter();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("No cases found"));
    }

    @Test
    public void testViewAllCasesError() {
        // Setup mock error response
        List<String> errors = new ArrayList<>();
        errors.add("Database error");
        when(caseService.getAllCases()).thenReturn(ApiResponse.error(errors, 500));

        // Execute method
        caseManagementConsole.viewAllCases();

        // Verify service call
        verify(caseService).getAllCases();
        verify(utils).waitForEnter();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Failed to retrieve cases"));
        assertTrue(output.contains("Database error"));
    }

    @Test
    public void testViewAllCasesException() {
        // Setup mock to throw exception
        when(caseService.getAllCases()).thenThrow(new RuntimeException("Connection failed"));

        // Execute method
        caseManagementConsole.viewAllCases();

        // Verify service call
        verify(caseService).getAllCases();
        verify(utils).waitForEnter();
        verify(mockLogger).error(eq("Error retrieving all cases: "), any(RuntimeException.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("An error occurred: Connection failed"));
    }

    @Test
    public void testSearchCaseById() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("1\n");

        // Setup mock response
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Execute method
        caseManagementConsole.searchCaseById();

        // Verify service call
        verify(caseService).getCaseById(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Search Case by ID"));
        assertTrue(output.contains("Case ID: 1"));
        assertTrue(output.contains("Test Civil Case"));
    }

    @Test
    public void testSearchCaseByIdNotFound() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("999\n");

        // Setup mock response
        List<String> errors = new ArrayList<>();
        errors.add("Case not found");
        when(caseService.getCaseById(999L)).thenReturn(ApiResponse.error(errors, 404));

        // Execute method
        caseManagementConsole.searchCaseById();

        // Verify service call
        verify(caseService).getCaseById(999L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case not found"));
    }

    @Test
    public void testSearchCaseByIdInvalidInput() {
        // Setup invalid input
        caseManagementConsole = createConsoleWithInput("abc\n");

        // Execute method
        caseManagementConsole.searchCaseById();

        // Verify service call (should not be called with invalid input)
        verify(caseService, never()).getCaseById(anyLong());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format"));
    }

    @Test
    public void testSearchCaseByIdException() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("1\n");

        // Setup mock to throw exception
        when(caseService.getCaseById(1L)).thenThrow(new RuntimeException("Connection failed"));

        // Execute method
        caseManagementConsole.searchCaseById();

        // Verify service call
        verify(caseService).getCaseById(1L);
        verify(mockLogger).error(eq("Error searching case by ID: "), any(RuntimeException.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("An error occurred: Connection failed"));
    }

    @Test
    public void testSearchCaseByCaseNumber() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("C-001\n");

        // Setup mock response
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseByCaseNumber("C-001")).thenReturn(ApiResponse.success(testCase));

        // Execute method
        caseManagementConsole.searchCaseByCaseNumber();

        // Verify service call
        verify(caseService).getCaseByCaseNumber("C-001");

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Search Case by Case Number"));
        assertTrue(output.contains("Case ID: 1"));
        assertTrue(output.contains("Test Civil Case"));
    }

    @Test
    public void testSearchCaseByCaseNumberNotFound() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("X-999\n");

        // Setup mock response
        List<String> errors = new ArrayList<>();
        errors.add("Case not found");
        when(caseService.getCaseByCaseNumber("X-999")).thenReturn(ApiResponse.error(errors, 404));

        // Execute method
        caseManagementConsole.searchCaseByCaseNumber();

        // Verify service call
        verify(caseService).getCaseByCaseNumber("X-999");

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case not found"));
    }

    @Test
    public void testFilterCasesByStatus() {
        // Setup for status selection
        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5))).thenReturn(2); // ACTIVE

            // Setup mock response for ACTIVE status
            List<Case> activeCases = createTestCases().stream()
                    .filter(c -> c.getStatus() == CaseStatus.ACTIVE)
                    .toList();
            when(caseService.getCasesByStatus(CaseStatus.ACTIVE)).thenReturn(ApiResponse.success(activeCases));

            // Execute method
            caseManagementConsole.filterCasesByStatus();

            // Verify service call
            verify(caseService).getCasesByStatus(CaseStatus.ACTIVE);

            // Check output contains expected content
            String output = outContent.toString();
            assertTrue(output.contains("Filter Cases by Status"));
            assertTrue(output.contains("Test Civil Case")); // The ACTIVE case
        }
    }

    @Test
    public void testFilterCasesByStatusNoResults() {
        // Setup for status selection
        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5))).thenReturn(5); // ARCHIVED

            // Setup mock response with no results
            when(caseService.getCasesByStatus(CaseStatus.ARCHIVED)).thenReturn(ApiResponse.success(new ArrayList<>()));

            // Execute method
            caseManagementConsole.filterCasesByStatus();

            // Verify service call
            verify(caseService).getCasesByStatus(CaseStatus.ARCHIVED);

            // Check output contains expected content
            String output = outContent.toString();
            assertTrue(output.contains("No cases found"));
        }
    }

    @Test
    public void testCreateNewCase() {
        // Setup input sequence: case number, title, description, case type, no for client assignment
        String input = "C-100\nNew Test Case\nThis is a test description\nN\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Mocking getUserChoice for case type selection
        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5))).thenReturn(1); // CIVIL

            // Setup mock response
            Case newCase = new Case(10L, "C-100", "New Test Case", CaseType.CIVIL);
            newCase.setStatus(CaseStatus.NEW);
            newCase.setDescription("This is a test description");
            when(caseService.createCase(any(Case.class))).thenReturn(ApiResponse.success(newCase));

            // Execute method
            caseManagementConsole.createNewCase();

            // Verify service call
            verify(caseService).createCase(any(Case.class));

            // Check output contains expected content
            String output = outContent.toString();
            assertTrue(output.contains("Case created successfully"));
        }
    }

    @Test
    public void testCreateNewCaseWithClientAssignment() {
        // Setup input sequence: case number, title, description, case type, yes for client assignment, client selections
        String input = "C-101\nNew Case With Clients\nCase with client assignment\nY\n1,2\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Mocking getUserChoice for case type selection
        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5))).thenReturn(3); // FAMILY

            // Setup mock responses
            Case newCase = new Case(11L, "C-101", "New Case With Clients", CaseType.FAMILY);
            newCase.setStatus(CaseStatus.NEW);
            newCase.setDescription("Case with client assignment");
            when(caseService.createCase(any(Case.class))).thenReturn(ApiResponse.success(newCase));

            // Setup clients for assignment
            List<Client> clients = createTestClients();
            when(clientService.getAllClients()).thenReturn(ApiResponse.success(clients));

            // Mock successful update after client assignment
            when(caseService.updateCase(any(Case.class))).thenReturn(ApiResponse.success(newCase));

            // Execute method
            caseManagementConsole.createNewCase();


            // Check output contains expected content
            String output = outContent.toString();
            assertTrue(output.contains("Case created successfully"));
            assertFalse(output.contains("Successfully assigned"));
        }
    }

    @Test
    public void testCreateNewCaseFailure() {
        // Setup input
        String input = "C-102\nFailed Case\nThis case will fail to create\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Mocking getUserChoice
        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5))).thenReturn(2); // CRIMINAL

            // Setup mock error response
            List<String> errors = new ArrayList<>();
            errors.add("Database constraint violation");
            when(caseService.createCase(any(Case.class))).thenReturn(ApiResponse.error(errors, 400));

            // Execute method
            caseManagementConsole.createNewCase();

            // Verify service call
            verify(caseService).createCase(any(Case.class));
            verify(clientService, never()).getAllClients(); // Should not try to assign clients

            // Check output contains expected content
            String output = outContent.toString();
            assertTrue(output.contains("Failed to create case"));
            assertTrue(output.contains("Database constraint violation"));
        }
    }

    @Test
    public void testUpdateCase() {
        // Setup input: ID, keep case number (empty), update title, keep description, keep type, update status
        String input = "1\n\nUpdated Case Title\n\n\n2\nN\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Case existingCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(existingCase));

        Case updatedCase = new Case(1L, existingCase.getCaseNumber(), "Updated Case Title", existingCase.getType());
        updatedCase.setStatus(CaseStatus.ACTIVE); // Status changed to ACTIVE (2)
        updatedCase.setDescription(existingCase.getDescription());
        when(caseService.updateCase(any(Case.class))).thenReturn(ApiResponse.success(updatedCase));

        // Execute method
        caseManagementConsole.updateCase();

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(caseService).updateCase(any(Case.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Update Existing Case"));
        assertTrue(output.contains("Case updated successfully"));
    }

    @Test
    public void testUpdateCaseNotFound() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("999\n");

        // Setup mock response
        List<String> errors = new ArrayList<>();
        errors.add("Case not found");
        when(caseService.getCaseById(999L)).thenReturn(ApiResponse.error(errors, 404));

        // Execute method
        caseManagementConsole.updateCase();

        // Verify service call
        verify(caseService).getCaseById(999L);
        verify(caseService, never()).updateCase(any(Case.class)); // Update should not be called

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case not found"));
    }

    @Test
    public void testUpdateCaseWithClientManagement() {
        // Setup input: ID, keep all fields, yes for client management, select clients
        String input = "1\n\n\n\n\n\nY\n1,3\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Case existingCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(existingCase));
        when(caseService.updateCase(any(Case.class))).thenReturn(ApiResponse.success(existingCase));

        // Setup clients for assignment
        List<Client> clients = createTestClients();
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(clients));

        // Execute method
        caseManagementConsole.updateCase();


        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case updated successfully"));
        assertTrue(output.contains("Successfully assigned"));
    }

    @Test
    public void testDeleteCase() {
        // Setup input with confirmation
        caseManagementConsole = createConsoleWithInput("1\nY\n");

        // Setup mock responses
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));
        when(caseService.deleteCase(1L)).thenReturn(ApiResponse.success(null));

        // Execute method
        caseManagementConsole.deleteCase();

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(caseService).deleteCase(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Delete Case"));
        assertTrue(output.contains("Case deleted successfully"));
    }

    @Test
    public void testDeleteCaseCancelled() {
        // Setup input with cancellation
        caseManagementConsole = createConsoleWithInput("1\nN\n");

        // Setup mock responses
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Execute method
        caseManagementConsole.deleteCase();

        // Verify get call but no delete
        verify(caseService).getCaseById(1L);
        verify(caseService, never()).deleteCase(anyLong());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case deletion cancelled"));
    }

    @Test
    public void testDeleteCaseNotFound() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("999\n");

        // Setup mock response
        List<String> errors = new ArrayList<>();
        errors.add("Case not found");
        when(caseService.getCaseById(999L)).thenReturn(ApiResponse.error(errors, 404));

        // Execute method
        caseManagementConsole.deleteCase();

        // Verify service call
        verify(caseService).getCaseById(999L);
        verify(caseService, never()).deleteCase(anyLong()); // Delete should not be called

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case not found"));
    }

    @Test
    public void testDeleteCaseFailure() {
        // Setup input with confirmation
        caseManagementConsole = createConsoleWithInput("1\nY\n");

        // Setup mock responses
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Mock deletion failure
        List<String> errors = new ArrayList<>();
        errors.add("Case has active hearings and cannot be deleted");
        when(caseService.deleteCase(1L)).thenReturn(ApiResponse.error(errors, 400));

        // Execute method
        caseManagementConsole.deleteCase();

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(caseService).deleteCase(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Failed to delete case"));
        assertTrue(output.contains("Case has active hearings"));
    }

    @Test
    public void testAssignClientsToCase() {
        // Setup input for client selection
        caseManagementConsole = createConsoleWithInput("1,3\n");

        // Setup mock responses
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Setup clients
        List<Client> clients = createTestClients();
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(clients));

        // Mock successful update
        when(caseService.updateCase(any(Case.class))).thenReturn(ApiResponse.success(testCase));

        // Execute method
        caseManagementConsole.assignClientsToCase(1L);

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(clientService).getAllClients();
        verify(caseService).updateCase(any(Case.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Assign Clients to Case"));
        assertTrue(output.contains("Successfully assigned"));
    }

    @Test
    public void testAssignClientsToCaseNoClients() {
        // Setup mock responses
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Mock empty client list
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute method
        caseManagementConsole.assignClientsToCase(1L);

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(clientService).getAllClients();
        verify(caseService, never()).updateCase(any(Case.class)); // No update should happen

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("No clients available to assign"));
    }

    @Test
    public void testAssignClientsToCaseNoSelection() {
        // Setup empty input (no selection)
        caseManagementConsole = createConsoleWithInput("\n");

        // Setup mock responses
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Setup clients
        List<Client> clients = createTestClients();
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(clients));

        // Execute method
        caseManagementConsole.assignClientsToCase(1L);

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(clientService).getAllClients();
        verify(caseService, never()).updateCase(any(Case.class)); // No update should happen

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("No clients selected for assignment"));
    }

    @Test
    public void testMenuNavigation() {
        // Create a console with mock Scanner to properly handle menu navigation
        ConsoleUtils consoleUtils = mock(ConsoleUtils.class);
        Scanner mockScanner = mock(Scanner.class);
        when(consoleUtils.getScanner()).thenReturn(mockScanner);
        when(consoleUtils.getLogger()).thenReturn(mockLogger);

        // Setup the mock Scanner to return values for the showMenu method
        // First return 1 for the initial menu (View All Cases), then 8 to exit
        when(mockScanner.nextLine()).thenReturn(""); // For waitForEnter call

        // Use MockedStatic for ConsoleUtils.getUserChoice
        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            // First call returns 1 (View All Cases), second call returns 8 (Exit)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(8)))
                    .thenReturn(1).thenReturn(8);

            // Setup test data
            List<Case> testCases = createTestCases();

            // Setup mock responses
            when(caseService.getAllCases()).thenReturn(ApiResponse.success(testCases));

            // Create console with our mocks
            CaseManagementConsole console = new CaseManagementConsole(caseService, clientService, consoleUtils);

            // Execute the menu
            console.showMenu(new User());

            // Mock getCasesByStatus
            List<Case> activeCases = testCases.stream()
                    .filter(c -> c.getStatus() == CaseStatus.ACTIVE)
                    .toList();
            when(caseService.getCasesByStatus(CaseStatus.ACTIVE)).thenReturn(ApiResponse.success(activeCases));

            // Mock createCase
            Case newCase = new Case(999L, "C-999", "Quick Test", CaseType.CORPORATE);
            newCase.setDescription("Just testing");
            newCase.setStatus(CaseStatus.NEW);
            when(caseService.createCase(any(Case.class))).thenReturn(ApiResponse.success(newCase));

            // Verify that getAllCases was called
            verify(caseService).getAllCases();

            // Verify menu was displayed
            String output = outContent.toString();
            assertTrue(output.contains("--- Case Management ---"));
        }
    }

    @Test
    public void testCompleteMenuFlow() {
        // This test simulates a user navigating through each menu option once

        // Mock the scanner behavior for each menu option
        Scanner sequenceScanner = mock(Scanner.class);
        // First we'll sequence through menu options 1-7, then exit with 8
        when(sequenceScanner.nextLine()).thenReturn("")  // waitForEnter after viewing all cases
                .thenReturn("1")                            // Case ID for search
                .thenReturn("")                             // waitForEnter after case search
                .thenReturn("C-001")                        // Case number for search
                .thenReturn("")                             // waitForEnter after case number search
                .thenReturn("")                             // waitForEnter after filtering
                .thenReturn("C-123")                        // New case number
                .thenReturn("Test Case")                    // New case title
                .thenReturn("Test description")             // New case description
                .thenReturn("N")                            // No to client assignment
                .thenReturn("")                             // waitForEnter after creation
                .thenReturn("1")                            // ID for update
                .thenReturn("")                             // Keep case number
                .thenReturn("")                             // Keep title
                .thenReturn("")                             // Keep description
                .thenReturn("")                             // Keep type
                .thenReturn("")                             // Keep status
                .thenReturn("N")                            // No to client management
                .thenReturn("")                             // waitForEnter after update
                .thenReturn("1")                            // ID for delete
                .thenReturn("N")                            // No to confirm delete
                .thenReturn("");                            // waitForEnter after cancel delete

        // Create mock utils with our scanner
        ConsoleUtils mockUtils = mock(ConsoleUtils.class);
        when(mockUtils.getScanner()).thenReturn(sequenceScanner);
        when(mockUtils.getLogger()).thenReturn(mockLogger);
        doNothing().when(mockUtils).waitForEnter();
        when(mockUtils.truncateString(anyString(), anyInt())).thenCallRealMethod();

        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            // Menu choices: 1-7 for operations, then 8 to exit
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(8)))
                    .thenReturn(1)      // View All Cases
                    .thenReturn(2)      // Search by ID
                    .thenReturn(3)      // Search by Case Number
                    .thenReturn(4)      // Filter by Status
                    .thenReturn(5)      // Create New Case
                    .thenReturn(6)      // Update Case
                    .thenReturn(7)      // Delete Case
                    .thenReturn(8);     // Exit

            // For submenu choices (case type, status, etc.)
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5)))
                    .thenReturn(1);     // Use CIVIL for type and NEW for status

            // Setup test data
            List<Case> cases = createTestCases();
            Case testCase = cases.get(0);

            // Setup mock responses for all service methods
            when(caseService.getAllCases()).thenReturn(ApiResponse.success(cases));
            when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));
            when(caseService.getCaseByCaseNumber("C-001")).thenReturn(ApiResponse.success(testCase));
            when(caseService.getCasesByStatus(any())).thenReturn(ApiResponse.success(List.of(testCase)));
            when(caseService.createCase(any())).thenReturn(ApiResponse.success(testCase));
            when(caseService.updateCase(any())).thenReturn(ApiResponse.success(testCase));

            // Create the console with our mocked dependencies
            CaseManagementConsole console = new CaseManagementConsole(caseService, clientService, mockUtils);

            // Execute the menu flow
            console.showMenu(new User());


            // We never confirmed the delete, so deleteCase shouldn't be called
            verify(caseService, never()).deleteCase(anyLong());
        }
    }

    @Test
    public void testMenuWithInvalidChoice() {
        // Setup input with an invalid menu choice followed by exit
        String input = "10\n8\n";
        caseManagementConsole = createConsoleWithInput(input);

        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            // First return invalid option 10, then valid exit option 8
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(8)))
                    .thenReturn(10).thenReturn(8);

            // Execute the method
            User mockUser = new User();
            caseManagementConsole.showMenu(mockUser);

            // Verify that error message was displayed
            String output = outContent.toString();
            assertTrue(output.contains("Invalid selection!"));
        }
    }

    @Test
    public void testInvalidCaseTypeSelection() {
        // Setup input for case creation with invalid type selection
        String input = "C-test\nTest Case\nTest description\nN\n";
        caseManagementConsole = createConsoleWithInput(input);

        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            // Return invalid option 99, which should default to OTHER
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(Scanner.class), eq(5)))
                    .thenReturn(99);

            // Mock case creation
            Case newCase = new Case();
            newCase.setId(100L);
            when(caseService.createCase(any(Case.class))).thenReturn(ApiResponse.success(newCase));

            // Execute the method
            caseManagementConsole.createNewCase();

            // Verify that we defaulted to OTHER and proceeded
            verify(caseService).createCase(argThat(caseArg ->
                    caseArg.getType() == CaseType.OTHER));

            String output = outContent.toString();
            assertTrue(output.contains("Invalid choice! Defaulting to OTHER"));
        }
    }

    @Test
    public void testDisplayCaseDetails() {
        // Create a case with all associations
        Case testCase = createTestCases().get(0);

        // Add clients
        List<Client> clients = createTestClients();
        testCase.setClients(clients);

        // Execute method to display case details
        caseManagementConsole.viewAllCases();

        // Setup mock to return our test case
        when(caseService.getAllCases()).thenReturn(ApiResponse.success(List.of(testCase)));

        // Re-execute to see the details
        caseManagementConsole.viewAllCases();

        // Verify output shows case details
        String output = outContent.toString();
        assertTrue(output.contains("Test Civil Case"));
        // Check for other expected parts in the display
    }

    @Test
    public void testTruncateStringInCaseDisplay() {
        // Create a case with very long title
        Case testCase = new Case(1L, "C-001",
                "This is an extremely long title that should be truncated when displayed in the case list view",
                CaseType.CIVIL);
        testCase.setStatus(CaseStatus.ACTIVE);

        // Setup mock to return our test case
        when(caseService.getAllCases()).thenReturn(ApiResponse.success(List.of(testCase)));

        // Execute method
        caseManagementConsole.viewAllCases();

        // Verify truncateString was called
        verify(utils).truncateString(
                eq("This is an extremely long title that should be truncated when displayed in the case list view"),
                eq(30));
    }

    /**
     * Test displayCaseDetails function with a case that has no related entities
     */
    @Test
    public void testDisplayCaseDetailsWithEmptyRelations() {
        // Create a basic case with no related entities
        Case testCase = new Case(1L, "C-001", "Basic Case", CaseType.CIVIL);
        testCase.setStatus(CaseStatus.NEW);
        testCase.setDescription("A basic case with no relations");
        testCase.setCreatedAt(LocalDateTime.now());
        testCase.setUpdatedAt(LocalDateTime.now());

        // Empty collections for relations
        testCase.setClients(new ArrayList<>());
        testCase.setHearings(new ArrayList<>());
        testCase.setDocuments(new ArrayList<>());

        // Setup mock to return our test case
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Create a console with input
        caseManagementConsole = createConsoleWithInput("1\n");

        // Call the method that will use displayCaseDetails
        caseManagementConsole.searchCaseById();

        // Verify the output contains all expected sections with empty notices
        String output = outContent.toString();

        // Basic case info
        assertTrue(output.contains("Case ID: 1"));
        assertTrue(output.contains("Title: Basic Case"));

        // Should show empty notices for all relations
        assertTrue(output.contains("No clients associated with this case"));
        assertTrue(output.contains("No hearings scheduled for this case"));
        assertTrue(output.contains("No documents associated with this case"));
    }

    /**
     * Test displayCaseDetails with null case (should handle gracefully)
     */
    @Test
    public void testDisplayCaseDetailsWithNullCase() {
        // Setup mock to return our test case
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(null));

        // Create a console with input
        caseManagementConsole = createConsoleWithInput("1\n");

        // Call the method that will use displayCaseDetails
        caseManagementConsole.searchCaseById();

        // Verify the output shows appropriate message
        String output = outContent.toString();
        assertTrue(output.contains("No case details available"));
    }

    /**
     * Test updateCase with valid case type input (1-5)
     */
    @Test
    public void testUpdateCaseWithValidTypeInput() {
        // Create test case with initial CIVIL type
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        testCase.setStatus(CaseStatus.ACTIVE);
        testCase.setDescription("Test description");

        // Setup mocks
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Update will be successful
        when(caseService.updateCase(any(Case.class))).thenAnswer(invocation -> {
            Case updatedCase = invocation.getArgument(0);
            return ApiResponse.success(updatedCase);
        });

        // Input sequence:
        // ID = 1
        // Keep case number (empty)
        // Keep title (empty)
        // Keep description (empty)
        // Change type to CRIMINAL (2)
        // Keep status (empty)
        // Don't manage clients (N)
        String input = "1\n\n\n\n2\n\nN\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Execute update method
        caseManagementConsole.updateCase();

        // Verify that type was updated to CRIMINAL
        verify(caseService).updateCase(argThat(caseArg ->
                caseArg.getType() == CaseType.CRIMINAL));

        // Verify output indicates success
        String output = outContent.toString();
        assertTrue(output.contains("Case updated successfully"));
    }

    /**
     * Test updateCase with invalid case type input (out of range)
     */
    @Test
    public void testUpdateCaseWithInvalidTypeInput() {
        // Create test case with initial CIVIL type
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        testCase.setStatus(CaseStatus.ACTIVE);
        testCase.setDescription("Test description");

        // Setup mocks
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Update will be successful
        when(caseService.updateCase(any(Case.class))).thenAnswer(invocation -> {
            Case updatedCase = invocation.getArgument(0);
            return ApiResponse.success(updatedCase);
        });

        // Input sequence:
        // ID = 1
        // Keep case number (empty)
        // Keep title (empty)
        // Keep description (empty)
        // Try invalid type (99)
        // Keep status (empty)
        // Don't manage clients (N)
        String input = "1\n\n\n\n99\n\nN\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Execute update method
        caseManagementConsole.updateCase();

        // Verify that type was kept as CIVIL (original value)
        verify(caseService).updateCase(argThat(caseArg ->
                caseArg.getType() == CaseType.CIVIL));

        // Verify error message was shown
        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice! Keeping current value"));
    }

    /**
     * Test updateCase with non-numeric case type input
     */
    @Test
    public void testUpdateCaseWithNonNumericTypeInput() {
        // Create test case with initial CIVIL type
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        testCase.setStatus(CaseStatus.ACTIVE);
        testCase.setDescription("Test description");

        // Setup mocks
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Update will be successful
        when(caseService.updateCase(any(Case.class))).thenAnswer(invocation -> {
            Case updatedCase = invocation.getArgument(0);
            return ApiResponse.success(updatedCase);
        });

        // Input sequence:
        // ID = 1
        // Keep case number (empty)
        // Keep title (empty)
        // Keep description (empty)
        // Try invalid type (abc - non-numeric)
        // Keep status (empty)
        // Don't manage clients (N)
        String input = "1\n\n\n\nabc\n\nN\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Execute update method
        caseManagementConsole.updateCase();

        // Verify that type was kept as CIVIL (original value)
        verify(caseService).updateCase(argThat(caseArg ->
                caseArg.getType() == CaseType.CIVIL));

        // Verify error message was shown
        String output = outContent.toString();
        assertTrue(output.contains("Invalid input! Keeping current value"));
    }

    /**
     * Test updateCase with each valid case type option
     */
    @Test
    public void testUpdateCaseWithEachValidType() {
        // For each valid type (1-5), we'll create a separate test
        CaseType[] expectedTypes = {
                CaseType.CIVIL,      // 1
                CaseType.CRIMINAL,   // 2
                CaseType.FAMILY,     // 3
                CaseType.CORPORATE,  // 4
                CaseType.OTHER       // 5
        };

        for (int typeChoice = 1; typeChoice <= 5; typeChoice++) {
            // Reset output capture
            outContent.reset();

            // Create test case with fixed initial type
            Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
            testCase.setStatus(CaseStatus.ACTIVE);

            // Setup mocks
            when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));
            when(caseService.updateCase(any(Case.class))).thenAnswer(invocation -> {
                Case updatedCase = invocation.getArgument(0);
                return ApiResponse.success(updatedCase);
            });

            // Input with the current type choice
            String input = "1\n\n\n\n" + typeChoice + "\n\nN\n";
            caseManagementConsole = createConsoleWithInput(input);

            // Execute update method
            caseManagementConsole.updateCase();

            // Verify correct type was set based on input
            CaseType expectedType = expectedTypes[typeChoice - 1];
            verify(caseService, atLeastOnce()).updateCase(argThat(caseArg ->
                    caseArg.getType() == expectedType));

            // Verify success message
            assertTrue(outContent.toString().contains("Case updated successfully"));

            // Reset mocks for next iteration
            reset(caseService);
        }
    }
}