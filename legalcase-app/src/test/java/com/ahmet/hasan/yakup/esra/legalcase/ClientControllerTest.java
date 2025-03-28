package com.ahmet.hasan.yakup.esra.legalcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ahmet.hasan.yakup.esra.legalcase.api.ClientController;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

/**
 * Test for ClientController using Mockito
 * This isolates tests from Spring context loading issues
 */
@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {

    @Mock
    private IClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private Client testClient;
    private List<Client> testClientList;
    private ApiResponse<Client> successResponse;
    private ApiResponse<Client> errorResponse;
    private ApiResponse<List<Client>> listSuccessResponse;

    @BeforeEach
    public void setup() {
        // Setup test data
        testClient = new Client();
        testClient.setId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");
        testClient.setEmail("john.doe@example.com");

        testClientList = new ArrayList<>();
        testClientList.add(testClient);

        // Create response objects
        successResponse = ApiResponse.success(testClient);
        errorResponse = ApiResponse.error("Test error message", HttpStatus.BAD_REQUEST.value());
        listSuccessResponse = ApiResponse.success(testClientList);
    }

    @Test
    public void testCreateClient_Success() {
        // Arrange
        when(clientService.createClient(any(Client.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Client>> response = clientController.createClient(testClient);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testClient, response.getBody().getData());

        // Verify service method was called
        verify(clientService).createClient(any(Client.class));
    }

    @Test
    public void testCreateClient_Failure() {
        // Arrange
        when(clientService.createClient(any(Client.class))).thenReturn(errorResponse);

        // Act
        ResponseEntity<ApiResponse<Client>> response = clientController.createClient(testClient);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorResponse.getErrorMessages().get(0), response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(clientService).createClient(any(Client.class));
    }

    @Test
    public void testGetClientById_Success() {
        // Arrange
        when(clientService.getClientById(anyLong())).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Client>> response = clientController.getClientById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testClient, response.getBody().getData());

        // Verify service method was called
        verify(clientService).getClientById(1L);
    }

    @Test
    public void testGetClientById_NotFound() {
        // Arrange
        when(clientService.getClientById(anyLong())).thenReturn(
                ApiResponse.error("Client not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Client>> response = clientController.getClientById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Client not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(clientService).getClientById(1L);
    }

    @Test
    public void testGetClientByEmail_Success() {
        // Arrange
        when(clientService.getClientByEmail(anyString())).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Client>> response =
                clientController.getClientByEmail("john.doe@example.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testClient, response.getBody().getData());

        // Verify service method was called
        verify(clientService).getClientByEmail("john.doe@example.com");
    }

    @Test
    public void testGetClientByEmail_NotFound() {
        // Arrange
        when(clientService.getClientByEmail(anyString())).thenReturn(
                ApiResponse.error("Client not found with email", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Client>> response =
                clientController.getClientByEmail("nonexistent@example.com");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Client not found with email", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(clientService).getClientByEmail("nonexistent@example.com");
    }

    @Test
    public void testGetAllClients() {
        // Arrange
        when(clientService.getAllClients()).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Client>>> response = clientController.getAllClients();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testClientList, response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());

        // Verify service method was called
        verify(clientService).getAllClients();
    }

    @Test
    public void testSearchClients() {
        // Arrange
        when(clientService.searchClients(anyString())).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Client>>> response = clientController.searchClients("John");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testClientList, response.getBody().getData());

        // Verify service method was called
        verify(clientService).searchClients("John");
    }

    @Test
    public void testUpdateClient_Success() {
        // Arrange
        when(clientService.updateClient(any(Client.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Client>> response = clientController.updateClient(1L, testClient);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testClient, response.getBody().getData());

        // Verify service method was called
        verify(clientService).updateClient(testClient);
    }

    @Test
    public void testUpdateClient_IdMismatch() {
        // Arrange
        testClient.setId(1L);

        // Act
        ResponseEntity<ApiResponse<Client>> response = clientController.updateClient(2L, testClient);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("ID in the URL does not match the ID in the request body", response.getBody().getErrorMessages().get(0));
    }

    @Test
    public void testUpdateClient_NotFound() {
        // Arrange
        when(clientService.updateClient(any(Client.class))).thenReturn(
                ApiResponse.error("Client not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Client>> response = clientController.updateClient(1L, testClient);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Client not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(clientService).updateClient(testClient);
    }

    @Test
    public void testDeleteClient_Success() {
        // Arrange
        when(clientService.deleteClient(anyLong())).thenReturn(ApiResponse.success(null));

        // Act
        ResponseEntity<ApiResponse<Void>> response = clientController.deleteClient(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        // Verify service method was called
        verify(clientService).deleteClient(1L);
    }

    @Test
    public void testDeleteClient_NotFound() {
        // Arrange
        when(clientService.deleteClient(anyLong())).thenReturn(
                ApiResponse.error("Client not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Void>> response = clientController.deleteClient(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Client not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(clientService).deleteClient(1L);
    }
}