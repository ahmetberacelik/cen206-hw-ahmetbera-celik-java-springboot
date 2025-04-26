package com.legalcase.client.application.service;

import com.legalcase.client.api.dto.request.ClientRequest;
import com.legalcase.client.api.dto.response.ClientResponse;
import com.legalcase.client.application.exception.ClientAlreadyExistsException;
import com.legalcase.client.application.exception.ClientNotFoundException;
import com.legalcase.client.application.mapper.ClientMapper;
import com.legalcase.client.domain.entity.Client;
import com.legalcase.client.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        logger.info("Creating client with name: {} {}", request.getName(), request.getSurname());
        
        // Email kontrolü
        if (request.getEmail() != null && !request.getEmail().isEmpty() && clientRepository.existsByEmail(request.getEmail())) {
            throw new ClientAlreadyExistsException("Bu e-posta ile kayıtlı bir müvekkil zaten mevcut: " + request.getEmail());
        }
        
        // TC kimlik kontrolü
        if (request.getIdentityNumber() != null && !request.getIdentityNumber().isEmpty() && 
                clientRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new ClientAlreadyExistsException("Bu kimlik numarası ile kayıtlı bir müvekkil zaten mevcut: " + request.getIdentityNumber());
        }
        
        // Vergi no kontrolü
        if (request.getTaxId() != null && !request.getTaxId().isEmpty() && 
                clientRepository.existsByTaxId(request.getTaxId())) {
            throw new ClientAlreadyExistsException("Bu vergi numarası ile kayıtlı bir müvekkil zaten mevcut: " + request.getTaxId());
        }
        
        Client client = clientMapper.toEntity(request);
        Client savedClient = clientRepository.save(client);
        
        logger.info("Client created successfully with ID: {}", savedClient.getId());
        return clientMapper.toResponse(savedClient);
    }
    
    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        logger.info("Retrieving client with ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Müvekkil bulunamadı: " + id));
        
        return clientMapper.toResponse(client);
    }
    
    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        logger.info("Retrieving all clients");
        
        List<Client> clients = clientRepository.findAll();
        return clientMapper.toResponseList(clients);
    }
    
    @Transactional(readOnly = true)
    public List<ClientResponse> getActiveClients() {
        logger.info("Retrieving active clients");
        
        List<Client> clients = clientRepository.findByActive(true);
        return clientMapper.toResponseList(clients);
    }
    
    @Transactional(readOnly = true)
    public List<ClientResponse> searchClients(String searchTerm) {
        logger.info("Searching clients with term: {}", searchTerm);
        
        List<Client> clients = clientRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(searchTerm, searchTerm);
        return clientMapper.toResponseList(clients);
    }
    
    @Transactional
    public ClientResponse updateClient(Long id, ClientRequest request) {
        logger.info("Updating client with ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Müvekkil bulunamadı: " + id));
        
        // Email benzersizlik kontrolü
        if (request.getEmail() != null && !request.getEmail().isEmpty() && 
                !request.getEmail().equals(client.getEmail()) && 
                clientRepository.existsByEmail(request.getEmail())) {
            throw new ClientAlreadyExistsException("Bu e-posta ile kayıtlı başka bir müvekkil zaten mevcut: " + request.getEmail());
        }
        
        // TC kimlik benzersizlik kontrolü
        if (request.getIdentityNumber() != null && !request.getIdentityNumber().isEmpty() && 
                !request.getIdentityNumber().equals(client.getIdentityNumber()) && 
                clientRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new ClientAlreadyExistsException("Bu kimlik numarası ile kayıtlı başka bir müvekkil zaten mevcut: " + request.getIdentityNumber());
        }
        
        // Vergi no benzersizlik kontrolü
        if (request.getTaxId() != null && !request.getTaxId().isEmpty() && 
                !request.getTaxId().equals(client.getTaxId()) && 
                clientRepository.existsByTaxId(request.getTaxId())) {
            throw new ClientAlreadyExistsException("Bu vergi numarası ile kayıtlı başka bir müvekkil zaten mevcut: " + request.getTaxId());
        }
        
        clientMapper.updateEntityFromRequest(client, request);
        Client updatedClient = clientRepository.save(client);
        
        logger.info("Client updated successfully with ID: {}", updatedClient.getId());
        return clientMapper.toResponse(updatedClient);
    }
    
    @Transactional
    public void deactivateClient(Long id) {
        logger.info("Deactivating client with ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Müvekkil bulunamadı: " + id));
        
        client.setActive(false);
        clientRepository.save(client);
        
        logger.info("Client deactivated successfully with ID: {}", id);
    }
    
    @Transactional
    public void activateClient(Long id) {
        logger.info("Activating client with ID: {}", id);
        
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Müvekkil bulunamadı: " + id));
        
        client.setActive(true);
        clientRepository.save(client);
        
        logger.info("Client activated successfully with ID: {}", id);
    }
    
    @Transactional
    public void deleteClient(Long id) {
        logger.info("Deleting client with ID: {}", id);
        
        if (!clientRepository.existsById(id)) {
            throw new ClientNotFoundException("Müvekkil bulunamadı: " + id);
        }
        
        clientRepository.deleteById(id);
        logger.info("Client deleted successfully with ID: {}", id);
    }
} 