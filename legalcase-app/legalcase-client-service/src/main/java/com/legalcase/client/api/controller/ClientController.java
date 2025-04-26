package com.legalcase.client.api.controller;

import com.legalcase.client.api.dto.request.ClientRequest;
import com.legalcase.client.api.dto.response.ClientResponse;
import com.legalcase.client.application.service.ClientService;
import com.legalcase.commons.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management API")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
    
    private final ClientService clientService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER')")
    @Operation(summary = "Create a new client", description = "Creates a new client with the provided information. Requires ADMIN or LAWYER role.")
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(@Valid @RequestBody ClientRequest request) {
        logger.info("REST request to create new client");
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER', 'ASSISTANT')")
    @Operation(summary = "Get client by ID", description = "Retrieves a client by their ID. Requires ADMIN, LAWYER or ASSISTANT role.")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable Long id) {
        logger.info("REST request to get client by id: {}", id);
        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER', 'ASSISTANT')")
    @Operation(
        summary = "Get all clients",
        description = "Retrieves all clients from the system. Requires ADMIN, LAWYER or ASSISTANT role.",
        security = {@SecurityRequirement(name = "bearerAuth")}
    )
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAllClients() {
        logger.info("REST request to get all clients");
        List<ClientResponse> response = clientService.getAllClients();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER', 'ASSISTANT')")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getActiveClients() {
        logger.info("REST request to get active clients");
        List<ClientResponse> response = clientService.getActiveClients();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER', 'ASSISTANT')")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> searchClients(@RequestParam String term) {
        logger.info("REST request to search clients with term: {}", term);
        List<ClientResponse> response = clientService.searchClients(term);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER')")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @PathVariable Long id, 
            @Valid @RequestBody ClientRequest request) {
        logger.info("REST request to update client with id: {}", id);
        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER')")
    public ResponseEntity<ApiResponse<Void>> deactivateClient(@PathVariable Long id) {
        logger.info("REST request to deactivate client with id: {}", id);
        clientService.deactivateClient(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAWYER')")
    public ResponseEntity<ApiResponse<Void>> activateClient(@PathVariable Long id) {
        logger.info("REST request to activate client with id: {}", id);
        clientService.activateClient(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        logger.info("REST request to delete client with id: {}", id);
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 