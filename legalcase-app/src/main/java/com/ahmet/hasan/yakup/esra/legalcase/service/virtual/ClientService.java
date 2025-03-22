package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.List;

public interface ClientService {
    ApiResponse<Client> createClient(Client client);
    ApiResponse<Client> getClientById(Long id);
    ApiResponse<Client> getClientByEmail(String email);
    ApiResponse<List<Client>> getAllClients();
    ApiResponse<List<Client>> searchClients(String searchTerm);
    ApiResponse<Client> updateClient(Client client);
    ApiResponse<Void> deleteClient(Long id);
}