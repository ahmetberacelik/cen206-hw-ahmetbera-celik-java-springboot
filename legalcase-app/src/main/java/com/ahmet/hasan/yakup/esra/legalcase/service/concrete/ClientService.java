package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.repository.ClientRepository;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the ClientService interface
 */
@Service
@Transactional
public class ClientService implements IClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ApiResponse<Client> createClient(Client client) {
        logger.info("Creating new client: {} {}", client.getName(), client.getSurname());

        // Check if email is already in use
        Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail());
        if (existingClient.isPresent()) {
            return ApiResponse.error("Email address '" + client.getEmail() + "' is already in use.",
                    HttpStatus.CONFLICT.value());
        }

        try {
            Client savedClient = clientRepository.save(client);
            return ApiResponse.success(savedClient);
        } catch (Exception e) {
            logger.error("Error while saving client", e);
            return ApiResponse.error("An unexpected error occurred: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Client> getClientById(Long id) {
        logger.info("Getting client by ID: {}", id);
        Optional<Client> clientOptional = clientRepository.findById(id);
        if (clientOptional.isPresent()) {
            return ApiResponse.success(clientOptional.get());
        } else {
            return ApiResponse.error("Client not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Client> getClientByEmail(String email) {
        logger.info("Getting client by email: {}", email);
        Optional<Client> clientOptional = clientRepository.findByEmail(email);
        if (clientOptional.isPresent()) {
            return ApiResponse.success(clientOptional.get());
        } else {
            return ApiResponse.error("Client not found with email: " + email,
                    HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Client>> getAllClients() {
        logger.info("Getting all clients");
        List<Client> clients = clientRepository.findAll();
        return ApiResponse.success(clients);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Client>> searchClients(String searchTerm) {
        logger.info("Searching clients with term: {}", searchTerm);
        List<Client> clients = clientRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
                searchTerm, searchTerm);
        return ApiResponse.success(clients);
    }

    @Override
    public ApiResponse<Client> updateClient(Client client) {
        logger.info("Updating client with ID: {}", client.getId());

        // Check if client exists
        if (!clientRepository.existsById(client.getId())) {
            return ApiResponse.error("Client not found with ID: " + client.getId(),
                    HttpStatus.NOT_FOUND.value());
        }

        // Check if email is already used by another client
        Optional<Client> existingClient = clientRepository.findByEmail(client.getEmail());
        if (existingClient.isPresent() && !existingClient.get().getId().equals(client.getId())) {
            return ApiResponse.error("Cannot update client: email address '" + client.getEmail() +
                    "' is already in use by another client.", HttpStatus.CONFLICT.value());
        }

        try {
            Client updatedClient = clientRepository.save(client);
            return ApiResponse.success(updatedClient);
        } catch (Exception e) {
            logger.error("Error while updating client", e);
            return ApiResponse.error("An unexpected error occurred: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Void> deleteClient(Long id) {
        logger.info("Deleting client with ID: {}", id);

        if (!clientRepository.existsById(id)) {
            return ApiResponse.error("Client not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }

        try {
            clientRepository.deleteById(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            logger.error("Error while deleting client", e);
            return ApiResponse.error("An unexpected error occurred: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}