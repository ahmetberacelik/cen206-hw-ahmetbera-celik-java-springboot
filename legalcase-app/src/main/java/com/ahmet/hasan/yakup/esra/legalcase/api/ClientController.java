package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ClientService;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Client entities
 */
@RestController
@RequestMapping("/clients")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Client>> createClient(@RequestBody Client client) {
        logger.info("REST request to create a new client");
        ApiResponse<Client> response = clientService.createClient(client);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Client>> getClientById(@PathVariable Long id) {
        logger.info("REST request to get client by ID: {}", id);
        ApiResponse<Client> response = clientService.getClientById(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<Client>> getClientByEmail(@PathVariable String email) {
        logger.info("REST request to get client by email: {}", email);
        ApiResponse<Client> response = clientService.getClientByEmail(email);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Client>>> getAllClients() {
        logger.info("REST request to get all clients");
        ApiResponse<List<Client>> response = clientService.getAllClients();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Client>>> searchClients(@RequestParam String term) {
        logger.info("REST request to search clients with term: {}", term);
        ApiResponse<List<Client>> response = clientService.searchClients(term);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Client>> updateClient(@PathVariable Long id, @RequestBody Client client) {
        logger.info("REST request to update client with ID: {}", id);
        if (!client.getId().equals(id)) {
            return new ResponseEntity<>(
                    ApiResponse.error("ID in the URL does not match the ID in the request body", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST
            );
        }
        ApiResponse<Client> response = clientService.updateClient(client);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        logger.info("REST request to delete client with ID: {}", id);
        ApiResponse<Void> response = clientService.deleteClient(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.valueOf(response.getErrorCode()));
    }
}