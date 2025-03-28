package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.repository.ClientRepository;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private Logger logger;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientService(clientRepository);
    }

    // Helper method to create a test client
    private Client createTestClient() {
        return new Client(1L, "John", "Doe", "john.doe@example.com");
    }

    // Helper method to create a list of test clients
    private List<Client> createTestClientsList() {
        List<Client> clients = new ArrayList<>();
        clients.add(new Client(1L, "John", "Doe", "john.doe@example.com"));
        clients.add(new Client(2L, "Jane", "Smith", "jane.smith@example.com"));
        clients.add(new Client(3L, "Bob", "Johnson", "bob.johnson@example.com"));
        return clients;
    }

    @Test
    void createClient_ValidClient_ReturnsSuccess() {
        // Arrange
        Client testClient = createTestClient();
        when(clientRepository.findByEmail(testClient.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        ApiResponse<Client> response = clientService.createClient(testClient);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testClient, response.getData());
        verify(clientRepository).findByEmail(testClient.getEmail());
        verify(clientRepository).save(testClient);
    }

    @Test
    void createClient_DuplicateEmail_ReturnsError() {
        // Arrange
        Client testClient = createTestClient();
        when(clientRepository.findByEmail(testClient.getEmail())).thenReturn(Optional.of(testClient));

        // Act
        ApiResponse<Client> response = clientService.createClient(testClient);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("already in use"));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void createClient_RepositoryException_ReturnsError() {
        // Arrange
        Client testClient = createTestClient();
        when(clientRepository.findByEmail(testClient.getEmail())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Client> response = clientService.createClient(testClient);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("An unexpected error occurred"));
        verify(clientRepository).save(testClient);
    }

    @Test
    void getClientById_ValidId_ReturnsClient() {
        // Arrange
        Client testClient = createTestClient();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // Act
        ApiResponse<Client> response = clientService.getClientById(1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testClient, response.getData());
        verify(clientRepository).findById(1L);
    }

    @Test
    void getClientById_InvalidId_ReturnsError() {
        // Arrange
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Client> response = clientService.getClientById(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Client not found"));
        verify(clientRepository).findById(999L);
    }

    @Test
    void getClientByEmail_ValidEmail_ReturnsClient() {
        // Arrange
        Client testClient = createTestClient();
        when(clientRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testClient));

        // Act
        ApiResponse<Client> response = clientService.getClientByEmail("john.doe@example.com");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testClient, response.getData());
        verify(clientRepository).findByEmail("john.doe@example.com");
    }

    @Test
    void getClientByEmail_InvalidEmail_ReturnsError() {
        // Arrange
        when(clientRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        ApiResponse<Client> response = clientService.getClientByEmail("nonexistent@example.com");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Client not found"));
        verify(clientRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getAllClients_ReturnsAllClients() {
        // Arrange
        List<Client> testClients = createTestClientsList();
        when(clientRepository.findAll()).thenReturn(testClients);

        // Act
        ApiResponse<List<Client>> response = clientService.getAllClients();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testClients, response.getData());
        assertEquals(3, response.getData().size());
        verify(clientRepository).findAll();
    }

    @Test
    void getAllClients_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(clientRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Client>> response = clientService.getAllClients();

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(clientRepository).findAll();
    }

    @Test
    void searchClients_ValidTerm_ReturnsMatchingClients() {
        // Arrange
        List<Client> matchedClients = List.of(
                new Client(1L, "John", "Doe", "john.doe@example.com"),
                new Client(2L, "Johnny", "Smith", "johnny.smith@example.com")
        );
        when(clientRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("John", "John"))
                .thenReturn(matchedClients);

        // Act
        ApiResponse<List<Client>> response = clientService.searchClients("John");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(matchedClients, response.getData());
        assertEquals(2, response.getData().size());
        verify(clientRepository).findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("John", "John");
    }

    @Test
    void searchClients_NoMatches_ReturnsEmptyList() {
        // Arrange
        when(clientRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("NonExistent", "NonExistent"))
                .thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Client>> response = clientService.searchClients("NonExistent");

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(clientRepository).findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase("NonExistent", "NonExistent");
    }

    @Test
    void updateClient_ValidClient_ReturnsUpdatedClient() {
        // Arrange
        Client testClient = createTestClient();
        testClient.setName("Updated Name");
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(clientRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        ApiResponse<Client> response = clientService.updateClient(testClient);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Updated Name", response.getData().getName());
        verify(clientRepository).existsById(1L);
        verify(clientRepository).findByEmail("john.doe@example.com");
        verify(clientRepository).save(testClient);
    }

    @Test
    void updateClient_NonExistentId_ReturnsError() {
        // Arrange
        Client testClient = createTestClient();
        when(clientRepository.existsById(1L)).thenReturn(false);

        // Act
        ApiResponse<Client> response = clientService.updateClient(testClient);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Client not found"));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void updateClient_EmailAlreadyInUse_ReturnsError() {
        // Arrange
        Client testClient = createTestClient();
        Client otherClient = new Client(2L, "Jane", "Smith", "john.doe@example.com");
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(clientRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(otherClient));

        // Act
        ApiResponse<Client> response = clientService.updateClient(testClient);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("already in use by another client"));
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void updateClient_RepositoryException_ReturnsError() {
        // Arrange
        Client testClient = createTestClient();
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(clientRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Client> response = clientService.updateClient(testClient);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("An unexpected error occurred"));
        verify(clientRepository).save(testClient);
    }

    @Test
    void deleteClient_ExistingClient_ReturnsSuccess() {
        // Arrange
        when(clientRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clientRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = clientService.deleteClient(1L);

        // Assert
        assertTrue(response.isSuccess());
        verify(clientRepository).existsById(1L);
        verify(clientRepository).deleteById(1L);
    }

    @Test
    void deleteClient_NonExistentClient_ReturnsError() {
        // Arrange
        when(clientRepository.existsById(999L)).thenReturn(false);

        // Act
        ApiResponse<Void> response = clientService.deleteClient(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Client not found"));
        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    void deleteClient_RepositoryException_ReturnsError() {
        // Arrange
        when(clientRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(clientRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = clientService.deleteClient(1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("An unexpected error occurred"));
        verify(clientRepository).deleteById(1L);
    }
}