package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ClientService;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the ClientService interface
 */
@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client createClient(Client client) {
        logger.info("Creating new client: {} {}", client.getName(), client.getSurname());
        return clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> getClientById(Long id) {
        logger.info("Getting client by ID: {}", id);
        return clientRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> getClientByEmail(String email) {
        logger.info("Getting client by email: {}", email);
        return clientRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        logger.info("Getting all clients");
        return clientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> searchClients(String searchTerm) {
        logger.info("Searching clients with term: {}", searchTerm);
        return clientRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Override
    public Client updateClient(Client client) {
        logger.info("Updating client with ID: {}", client.getId());
        return clientRepository.save(client);
    }

    @Override
    public void deleteClient(Long id) {
        logger.info("Deleting client with ID: {}", id);
        clientRepository.deleteById(id);
    }
}