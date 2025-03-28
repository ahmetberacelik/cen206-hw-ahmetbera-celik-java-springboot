package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.ClientManagementConsole;
import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ClientManagementConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private IClientService clientService;
    private ConsoleUtils utils;
    private Scanner testScanner;
    private Logger mockLogger;
    private ClientManagementConsole clientManagementConsole;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        // Set up mocks
        clientService = Mockito.mock(IClientService.class);
        mockLogger = Mockito.mock(Logger.class);

        testScanner = new Scanner(System.in);
        utils = Mockito.mock(ConsoleUtils.class);
        when(utils.getScanner()).thenReturn(testScanner);
        when(utils.getLogger()).thenReturn(mockLogger);

        // Mock waitForEnter method for all tests by default
        Mockito.doNothing().when(utils).waitForEnter();

        clientManagementConsole = new ClientManagementConsole(clientService, utils);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a new console instance with simulated user input
     */
    private ClientManagementConsole createConsoleWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(utils.getScanner()).thenReturn(scanner);
        return new ClientManagementConsole(clientService, utils);
    }

    /**
     * Helper method to create test client data
     */
    private List<Client> createTestClients() {
        List<Client> clients = new ArrayList<>();

        Client client1 = new Client();
        client1.setId(1L);
        client1.setName("John");
        client1.setSurname("Doe");
        client1.setEmail("john.doe@example.com");
        client1.setCreatedAt(LocalDateTime.now().minusDays(30));
        client1.setUpdatedAt(LocalDateTime.now().minusDays(15));

        Client client2 = new Client();
        client2.setId(2L);
        client2.setName("Jane");
        client2.setSurname("Smith");
        client2.setEmail("jane.smith@example.com");
        client2.setCreatedAt(LocalDateTime.now().minusDays(20));
        client2.setUpdatedAt(LocalDateTime.now().minusDays(10));

        // Adding cases to client1
        List<Case> cases = new ArrayList<>();
        Case case1 = new Case(1L, "C-001", "Contract Dispute", CaseType.CIVIL);
        case1.setStatus(CaseStatus.ACTIVE);
        cases.add(case1);
        client1.setCases(cases);

        clients.add(client1);
        clients.add(client2);

        return clients;
    }

    @Test
    public void testViewAllClients() {
        // Prepare test data
        List<Client> testClients = createTestClients();

        // Mock service response
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(testClients));

        // Also mock the truncateString method since it's used in displayClientsList
        when(utils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            return str.length() > maxLength ? str.substring(0, maxLength) : str;
        });

        // Execute the method
        clientManagementConsole.viewAllClients();

        // Verify interactions
        verify(clientService).getAllClients();
        verify(utils).waitForEnter();

        // Check output contains expected text
        String output = outContent.toString();
        assertTrue(output.contains("All Clients"));
        assertTrue(output.contains("Total clients: 2"));
    }

    @Test
    public void testViewAllClientsEmpty() {
        // Mock empty response
        when(clientService.getAllClients()).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute the method
        clientManagementConsole.viewAllClients();

        // Verify interactions
        verify(clientService).getAllClients();
        verify(utils).waitForEnter();

        // Check output contains expected text
        String output = outContent.toString();
        assertTrue(output.contains("No clients found"));
    }

    @Test
    public void testViewAllClientsError() {
        // Mock error response
        when(clientService.getAllClients()).thenReturn(ApiResponse.error("Database error", 500));

        // Execute the method
        clientManagementConsole.viewAllClients();

        // Verify interactions
        verify(clientService).getAllClients();
        verify(utils).waitForEnter();

        // Check output contains expected text
        String output = outContent.toString();
        assertTrue(output.contains("Failed to retrieve clients"));
    }

    @Test
    public void testSearchClientById() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("1\n");

        // Mock service response
        Client testClient = createTestClients().get(0);
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(testClient));

        // Execute the method
        clientManagementConsole.searchClientById();

        // Verify interactions
        verify(clientService).getClientById(1L);
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client ID: 1"));
        assertTrue(output.contains("John"));
        assertTrue(output.contains("Doe"));
    }

    @Test
    public void testSearchClientByIdNotFound() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("99\n");

        // Mock service response - not found
        when(clientService.getClientById(99L)).thenReturn(ApiResponse.error("Client not found", 404));

        // Execute the method
        clientManagementConsole.searchClientById();

        // Verify interactions
        verify(clientService).getClientById(99L);
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client not found"));
    }

    @Test
    public void testSearchClientByIdInvalidInput() {
        // Setup scanner with input (non-numeric)
        clientManagementConsole = createConsoleWithInput("abc\n");

        // Execute the method
        clientManagementConsole.searchClientById();

        // Verify interactions - service should not be called
        verify(clientService, never()).getClientById(anyLong());
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format"));
    }

    @Test
    public void testSearchClientByEmail() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("john.doe@example.com\n");

        // Mock service response
        Client testClient = createTestClients().get(0);
        when(clientService.getClientByEmail("john.doe@example.com")).thenReturn(ApiResponse.success(testClient));

        // Execute the method
        clientManagementConsole.searchClientByEmail();

        // Verify interactions
        verify(clientService).getClientByEmail("john.doe@example.com");
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client ID: 1"));
        assertTrue(output.contains("John"));
    }

    @Test
    public void testSearchClientByEmailNotFound() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("notfound@example.com\n");

        // Mock service response
        when(clientService.getClientByEmail("notfound@example.com"))
                .thenReturn(ApiResponse.error("Client not found", 404));

        // Execute the method
        clientManagementConsole.searchClientByEmail();

        // Verify interactions
        verify(clientService).getClientByEmail("notfound@example.com");
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client not found"));
    }

    @Test
    public void testSearchClientsByName() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("John\n");

        // Mock service response
        List<Client> foundClients = Arrays.asList(createTestClients().get(0));
        when(clientService.searchClients("John")).thenReturn(ApiResponse.success(foundClients));

        // Also mock the truncateString method since it's used in displayClientsList
        when(utils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            return str.length() > maxLength ? str.substring(0, maxLength) : str;
        });

        // Execute the method
        clientManagementConsole.searchClientsByName();

        // Verify interactions
        verify(clientService).searchClients("John");
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("John"));
        assertTrue(output.contains("Total clients: 1"));
    }

    @Test
    public void testSearchClientsByNameNotFound() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("Nobody\n");

        // Mock service response - empty list
        when(clientService.searchClients("Nobody")).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute the method
        clientManagementConsole.searchClientsByName();

        // Verify interactions
        verify(clientService).searchClients("Nobody");
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("No clients found"));
    }

    @Test
    public void testCreateNewClient() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("John\nDoe\njohn.doe@example.com\n");

        // Prepare client that will be returned
        Client newClient = new Client();
        newClient.setId(1L);
        newClient.setName("John");
        newClient.setSurname("Doe");
        newClient.setEmail("john.doe@example.com");

        // Mock service response
        when(clientService.createClient(any(Client.class))).thenReturn(ApiResponse.success(newClient));

        // Execute the method
        clientManagementConsole.createNewClient();

        // Verify interactions
        verify(clientService).createClient(any(Client.class));
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client created successfully"));
    }

    @Test
    public void testCreateNewClientError() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("John\nDoe\njohn.doe@example.com\n");

        // Mock service response - error
        when(clientService.createClient(any(Client.class)))
                .thenReturn(ApiResponse.error("Email already exists", 400));

        // Execute the method
        clientManagementConsole.createNewClient();

        // Verify interactions
        verify(clientService).createClient(any(Client.class));
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Failed to create client"));
    }

    @Test
    public void testUpdateClient() {
        // Setup scanner with input for ID and new values
        clientManagementConsole = createConsoleWithInput("1\nJohnny\n\nnewmail@example.com\n");

        // Prepare client that will be returned from get and update
        Client existingClient = createTestClients().get(0);
        Client updatedClient = new Client();
        updatedClient.setId(1L);
        updatedClient.setName("Johnny");
        updatedClient.setSurname("Doe");
        updatedClient.setEmail("newmail@example.com");

        // Mock service responses
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(existingClient));
        when(clientService.updateClient(any(Client.class))).thenReturn(ApiResponse.success(updatedClient));

        // Execute the method
        clientManagementConsole.updateClient();

        // Verify interactions
        verify(clientService).getClientById(1L);
        verify(clientService).updateClient(any(Client.class));
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client updated successfully"));
    }

    @Test
    public void testUpdateClientNotFound() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("99\n");

        // Mock service response - not found
        when(clientService.getClientById(99L)).thenReturn(ApiResponse.error("Client not found", 404));

        // Execute the method
        clientManagementConsole.updateClient();

        // Verify interactions - only verify the service call, not waitForEnter
        verify(clientService).getClientById(99L);
        verify(clientService, never()).updateClient(any(Client.class));

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client not found"));
    }

    @Test
    public void testUpdateClientInvalidInput() {
        // Setup scanner with input (non-numeric)
        clientManagementConsole = createConsoleWithInput("abc\n");

        // Execute the method
        clientManagementConsole.updateClient();

        // Verify interactions - service should not be called
        verify(clientService, never()).getClientById(anyLong());
        verify(clientService, never()).updateClient(any(Client.class));
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format"));
    }

    @Test
    public void testDeleteClient() {
        // Setup scanner with input for ID and confirmation
        clientManagementConsole = createConsoleWithInput("1\nY\n");

        // Mock service responses
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(createTestClients().get(0)));
        when(clientService.deleteClient(1L)).thenReturn(ApiResponse.success(null));

        // Execute the method
        clientManagementConsole.deleteClient();

        // Verify interactions
        verify(clientService).getClientById(1L);
        verify(clientService).deleteClient(1L);
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client deleted successfully"));
    }

    @Test
    public void testDeleteClientNotFound() {
        // Setup scanner with input
        clientManagementConsole = createConsoleWithInput("99\n");

        // Mock service response - not found
        when(clientService.getClientById(99L)).thenReturn(ApiResponse.error("Client not found", 404));

        // Execute the method
        clientManagementConsole.deleteClient();

        // Verify interactions - only verify the service call, not waitForEnter
        verify(clientService).getClientById(99L);
        verify(clientService, never()).deleteClient(anyLong());

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client not found"));
    }

    @Test
    public void testDeleteClientCancelled() {
        // Setup scanner with input for ID and cancellation
        clientManagementConsole = createConsoleWithInput("1\nN\n");

        // Mock service responses
        when(clientService.getClientById(1L)).thenReturn(ApiResponse.success(createTestClients().get(0)));

        // Execute the method
        clientManagementConsole.deleteClient();

        // Verify interactions
        verify(clientService).getClientById(1L);
        verify(clientService, never()).deleteClient(anyLong());
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Client deletion cancelled"));
    }

    @Test
    public void testDeleteClientInvalidInput() {
        // Setup scanner with input (non-numeric)
        clientManagementConsole = createConsoleWithInput("abc\n");

        // Execute the method
        clientManagementConsole.deleteClient();

        // Verify interactions - service should not be called
        verify(clientService, never()).getClientById(anyLong());
        verify(clientService, never()).deleteClient(anyLong());
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format"));
    }

    @Test
    public void testMenuComprehensive() {
        // Prepare a comprehensive input sequence that will exercise all menu options
        String input =
                "1\n" +  // View All Clients
                        "2\n1\n" +  // Search Client by ID
                        "3\njohn.doe@example.com\n" +  // Search Client by Email
                        "4\nDoe\n" +  // Search Clients by Name

                        // Create New Client
                        "5\n" +
                        "Alice\n" +  // First Name
                        "Johnson\n" +  // Last Name
                        "alice.j@example.com\n" +  // Email

                        // Update Client
                        "6\n1\n" +  // Client ID
                        "John\n" +  // First Name (unchanged)
                        "Smith\n" +  // Last Name (changed)
                        "\n" +  // Email (unchanged)

                        // Delete Client (with confirmation)
                        "7\n2\nY\n" +

                        "8\n";  // Return to Main Menu

        clientManagementConsole = createConsoleWithInput(input);

        // Setup mock responses for each interaction
        List<Client> testClients = createTestClients();
        Client testClient = testClients.get(0);
        Client testClient2 = testClients.get(1);

        // Mock responses for getAllClients
        when(clientService.getAllClients())
                .thenReturn(ApiResponse.success(testClients));

        // Mock responses for getClientById - use atLeastOnce() since it will be called multiple times
        when(clientService.getClientById(1L))
                .thenReturn(ApiResponse.success(testClient));

        // Mock responses for getClientByEmail
        when(clientService.getClientByEmail("john.doe@example.com"))
                .thenReturn(ApiResponse.success(testClient));

        // Mock responses for searchClients
        when(clientService.searchClients("Doe"))
                .thenReturn(ApiResponse.success(Arrays.asList(testClient)));

        // Mock client creation
        Client newClient = new Client();
        newClient.setId(3L);
        newClient.setName("Alice");
        newClient.setSurname("Johnson");
        newClient.setEmail("alice.j@example.com");
        when(clientService.createClient(any(Client.class)))
                .thenReturn(ApiResponse.success(newClient));

        // Mock client update
        Client updatedClient = new Client();
        updatedClient.setId(1L);
        updatedClient.setName("John");
        updatedClient.setSurname("Smith");
        updatedClient.setEmail("john.doe@example.com");
        when(clientService.updateClient(any(Client.class)))
                .thenReturn(ApiResponse.success(updatedClient));

        // Mock client deletion
        when(clientService.getClientById(2L))
                .thenReturn(ApiResponse.success(testClient2));
        when(clientService.deleteClient(2L))
                .thenReturn(ApiResponse.success(null));

        // Execute the method with a mock user
        clientManagementConsole.showMenu(new User());

        // Verify interactions for all menu options
        verify(clientService).getAllClients();
        verify(clientService, atLeastOnce()).getClientById(1L); // Using atLeastOnce() since it's called multiple times
        verify(clientService).getClientByEmail("john.doe@example.com");
        verify(clientService).searchClients("Doe");
        verify(clientService).createClient(any(Client.class));
        verify(clientService).updateClient(any(Client.class));
        verify(clientService).getClientById(2L);
        verify(clientService).deleteClient(2L);
    }

    @Test
    public void testMenuWithInvalidSelections() {
        // Prepare input with invalid menu selection followed by exit
        String input = "10\n8\n";
        clientManagementConsole = createConsoleWithInput(input);

        // Execute method
        clientManagementConsole.showMenu(new User());

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Invalid selection"));
    }

    @Test
    public void testMenuWithErrorResponses() {
        // Prepare input sequence for menu options that will receive error responses
        String input =
                "1\n" +  // View All Clients (will error)
                        "2\n1\n" +  // Search Client by ID (will error)
                        "8\n";  // Return to Main Menu

        clientManagementConsole = createConsoleWithInput(input);

        // Mock error responses
        when(clientService.getAllClients())
                .thenReturn(ApiResponse.error("Database connection error", 500));
        when(clientService.getClientById(1L))
                .thenReturn(ApiResponse.error("Client not found", 404));

        // Execute the method
        clientManagementConsole.showMenu(new User());

        // Verify interactions
        verify(clientService).getAllClients();
        verify(clientService).getClientById(1L);

        // Check output for error messages
        String output = outContent.toString();
        assertTrue(output.contains("Failed to retrieve clients"));
        assertTrue(output.contains("Client not found"));
    }
}