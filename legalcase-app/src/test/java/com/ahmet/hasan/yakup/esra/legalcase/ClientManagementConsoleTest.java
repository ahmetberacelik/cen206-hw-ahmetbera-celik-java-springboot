package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.console.ClientManagementConsole;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
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

public class ClientManagementConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private IClientService clientService;
    private ConsoleUtils utils;
    private Scanner testScanner;
    private Logger mockLogger;
    private ClientManagementConsole clientManagementConsole;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        // Setup mocks
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

        clientManagementConsole = new ClientManagementConsole(clientService, utils);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a console with simulated input
     */
    private ClientManagementConsole createConsoleWithInput(String input) {
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

        return new ClientManagementConsole(clientService, consoleUtils);
    }

    /**
     * Helper method to create test client data
     */
    private List<Client> createTestClients() {
        List<Client> clients = new ArrayList<>();

        Client client1 = new Client(1L, "John", "Doe", "john.doe@example.com");
        Client client2 = new Client(2L, "Jane", "Smith", "jane.smith@example.com");

        // Add some cases to the clients if needed
        Case case1 = new Case();
        case1.setId(1L);
        case1.setCaseNumber("C-001");
        case1.setTitle("Test Case 1");

        client1.getCases().add(case1);

        clients.add(client1);
        clients.add(client2);

        return clients;
    }

    @Test
    public void testViewAllClients() {
        // Setup mock response
        List<Client> testClients = createTestClients();
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(testClients));

        // Execute method
        clientManagementConsole.viewAllClients();

        // Verify service call
        verify(clientService).getAllClients();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("All Clients"));
    }

    @Test
    public void testViewAllClientsEmpty() {
        // Setup mock empty response
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute method
        clientManagementConsole.viewAllClients();

        // Verify service call
        verify(clientService).getAllClients();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("No clients found"));
    }

    @Test
    public void testViewAllClientsError() {
        // Setup mock error response
        when(clientService.getAllClients()).thenReturn(ApiResponse.error("Database error", 500));

        // Execute method
        clientManagementConsole.viewAllClients();

        // Verify service call
        verify(clientService).getAllClients();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Failed to retrieve clients"));
    }

    @Test
    public void testSearchClientById() {
        // Setup input
        clientManagementConsole = createConsoleWithInput("1\n");

        // Setup mock response
        Client testClient = createTestClients().get(0);
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(testClient));

        // Execute method
        clientManagementConsole.searchClientById();

        // Verify service call
        verify(clientService).getClientById(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Search Client by ID"));
    }

    @Test
    public void testSearchClientByIdNotFound() {
        // Setup input
        clientManagementConsole = createConsoleWithInput("999\n");

        // Setup mock response
        when(clientService.getClientById(999L)).thenReturn(ApiResponse.error("Client not found", 404));

        // Execute method
        clientManagementConsole.searchClientById();

        // Verify service call
        verify(clientService).getClientById(999L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Client not found"));
    }

    @Test
    public void testSearchClientByIdInvalidInput() {
        // Setup invalid input
        clientManagementConsole = createConsoleWithInput("abc\n");

        // Execute method
        clientManagementConsole.searchClientById();

        // Verify service call (should not be called with invalid input)
        verify(clientService, never()).getClientById(anyLong());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format"));
    }

    @Test
    public void testSearchClientByEmail() {
        // Setup input
        clientManagementConsole = createConsoleWithInput("john.doe@example.com\n");

        // Setup mock response
        Client testClient = createTestClients().get(0);
        when(clientService.getClientByEmail("john.doe@example.com")).thenReturn(ApiResponse.success(testClient));

        // Execute method
        clientManagementConsole.searchClientByEmail();

        // Verify service call
        verify(clientService).getClientByEmail("john.doe@example.com");

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Search Client by Email"));
    }

    @Test
    public void testSearchClientsByName() {
        // Setup input
        clientManagementConsole = createConsoleWithInput("Doe\n");

        // Setup mock response
        List<Client> testClients = new ArrayList<>();
        testClients.add(createTestClients().get(0)); // Only client with "Doe"
        when(clientService.searchClients("Doe")).thenReturn(ApiResponse.success(testClients));

        // Execute method
        clientManagementConsole.searchClientsByName();

        // Verify service call
        verify(clientService).searchClients("Doe");

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Search Clients by Name"));
    }

    @Test
    public void testCreateNewClient() {
        // Setup input sequence: name, surname, email
        String input = "John\nDoe\njohn.doe@example.com\n";
        clientManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Client createdClient = new Client(3L, "John", "Doe", "john.doe@example.com");
        when(clientService.createClient(any(Client.class))).thenReturn(ApiResponse.success(createdClient));

        // Execute method
        clientManagementConsole.createNewClient();

        // Verify service call
        verify(clientService).createClient(any(Client.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Client created successfully"));
    }

    @Test
    public void testUpdateClient() {
        // Setup input sequence: ID, new name, new surname, new email
        String input = "1\n\nNewDoe\n\n";
        clientManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Client existingClient = createTestClients().get(0);
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(existingClient));

        Client updatedClient = new Client(1L, existingClient.getName(), "NewDoe", existingClient.getEmail());
        when(clientService.updateClient(any(Client.class))).thenReturn(ApiResponse.success(updatedClient));

        // Execute method
        clientManagementConsole.updateClient();

        // Verify service calls
        verify(clientService).getClientById(1L);
        verify(clientService).updateClient(any(Client.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Update Client Details"));
    }

    @Test
    public void testDeleteClient() {
        // Setup input with confirmation
        clientManagementConsole = createConsoleWithInput("1\nY\n");

        // Setup mock responses
        Client testClient = createTestClients().get(0);
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(testClient));
        when(clientService.deleteClient(1L)).thenReturn(ApiResponse.success(null));

        // Execute method
        clientManagementConsole.deleteClient();

        // Verify service calls
        verify(clientService).getClientById(1L);
        verify(clientService).deleteClient(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Delete Client"));
    }

    @Test
    public void testDeleteClientCancelled() {
        // Setup input with cancellation
        clientManagementConsole = createConsoleWithInput("1\nN\n");

        // Setup mock responses
        Client testClient = createTestClients().get(0);
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(testClient));

        // Execute method
        clientManagementConsole.deleteClient();

        // Verify get call but no delete
        verify(clientService).getClientById(1L);
        verify(clientService, never()).deleteClient(anyLong());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Client deletion cancelled"));
    }
}