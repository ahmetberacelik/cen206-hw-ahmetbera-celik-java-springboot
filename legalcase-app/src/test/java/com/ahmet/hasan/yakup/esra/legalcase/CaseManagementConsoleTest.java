package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.console.CaseManagementConsole;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;
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

    @Before
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

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a console with simulated input
     */
    private CaseManagementConsole createConsoleWithInput(String input) {
        Scanner scanner = new Scanner(new java.io.ByteArrayInputStream(input.getBytes()));
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

        cases.add(case1);
        cases.add(case2);

        return cases;
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

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("All Cases"));
    }

    @Test
    public void testViewAllCasesEmpty() {
        // Setup mock empty response
        when(caseService.getAllCases()).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute method
        caseManagementConsole.viewAllCases();

        // Verify service call
        verify(caseService).getAllCases();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("No cases found"));
    }

    @Test
    public void testViewAllCasesError() {
        // Setup mock error response
        when(caseService.getAllCases()).thenReturn(ApiResponse.error("Database error", 500));

        // Execute method
        caseManagementConsole.viewAllCases();

        // Verify service call
        verify(caseService).getAllCases();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Failed to retrieve cases"));
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
    }

    @Test
    public void testSearchCaseByIdNotFound() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("999\n");

        // Setup mock response
        when(caseService.getCaseById(999L)).thenReturn(ApiResponse.error("Case not found", 404));

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
    }

    @Test
    public void testFilterCasesByStatus() {
        // Setup input
        caseManagementConsole = createConsoleWithInput("2\n");

        // Setup mock response for ACTIVE status
        List<Case> testCases = createTestCases().stream()
                .filter(c -> c.getStatus() == CaseStatus.ACTIVE)
                .toList();
        when(caseService.getCasesByStatus(CaseStatus.ACTIVE)).thenReturn(ApiResponse.success(testCases));

        // Mocking getUserChoice for status selection
        try (var mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(), eq(5))).thenReturn(2);

            // Execute method
            caseManagementConsole.filterCasesByStatus();
        }

        // Verify service call
        verify(caseService).getCasesByStatus(CaseStatus.ACTIVE);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Filter Cases by Status"));
    }

    @Test
    public void testCreateNewCase() {
        // Setup input sequence: case number, title, description, case type
        String input = "C-003\nTest New Case\nA new test case\n1\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Mocking getUserChoice for case type selection
        try (var mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(), eq(5))).thenReturn(1);

            // Setup mock responses
            Case createdCase = new Case(3L, "C-003", "Test New Case", CaseType.CIVIL);
            createdCase.setStatus(CaseStatus.NEW);
            when(caseService.createCase(any(Case.class))).thenReturn(ApiResponse.success(createdCase));

            // Setup input for client assignment
            when(utils.getScanner().nextLine()).thenReturn("N");

            // Execute method
            caseManagementConsole.createNewCase();
        }

        // Verify service call
        verify(caseService).createCase(any(Case.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case created successfully"));
    }

    @Test
    public void testUpdateCase() {
        // Setup input sequence: ID, new case number, new title, type selection, status selection
        String input = "1\n\nUpdated Case Title\n\n\nN\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Case existingCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(existingCase));

        Case updatedCase = new Case(1L, existingCase.getCaseNumber(), "Updated Case Title", existingCase.getType());
        updatedCase.setStatus(existingCase.getStatus());
        when(caseService.updateCase(any(Case.class))).thenReturn(ApiResponse.success(updatedCase));

        // Execute method
        caseManagementConsole.updateCase();

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(caseService).updateCase(any(Case.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Update Existing Case"));
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
    public void testAssignClientsToCase() {
        // Setup input with client selection
        String input = "1,2\n";
        caseManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Case testCase = createTestCases().get(0);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        // Prepare client list for selection
        List<Client> clients = new ArrayList<>();
        Client client1 = new Client(1L, "John", "Doe", "john@example.com");
        Client client2 = new Client(2L, "Jane", "Smith", "jane@example.com");
        clients.add(client1);
        clients.add(client2);
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(clients));

        // Mock case update response
        when(caseService.updateCase(any(Case.class))).thenReturn(ApiResponse.success(testCase));

        // Execute method
        caseManagementConsole.assignClientsToCase(1L);

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(clientService).getAllClients();
        verify(caseService).updateCase(any(Case.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Successfully assigned"));
    }
}