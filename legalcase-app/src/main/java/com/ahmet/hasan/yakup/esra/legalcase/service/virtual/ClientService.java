package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Client entity operations
 */
public interface ClientService {
    /**
     * Creates a new client
     * @param client the client to create
     * @return the created client
     */
    Client createClient(Client client);

    /**
     * Retrieves a client by its ID
     * @param id the client ID
     * @return an Optional containing the client if found
     */
    Optional<Client> getClientById(Long id);

    /**
     * Retrieves a client by email address
     * @param email the client's email
     * @return an Optional containing the client if found
     */
    Optional<Client> getClientByEmail(String email);

    /**
     * Retrieves all clients
     * @return a list of all clients
     */
    List<Client> getAllClients();

    /**
     * Searches for clients by name or surname
     * @param searchTerm the search term to match against name or surname
     * @return a list of matching clients
     */
    List<Client> searchClients(String searchTerm);

    /**
     * Updates an existing client
     * @param client the client with updated data
     * @return the updated client
     */
    Client updateClient(Client client);

    /**
     * Deletes a client by ID
     * @param id the ID of the client to delete
     */
    void deleteClient(Long id);
}