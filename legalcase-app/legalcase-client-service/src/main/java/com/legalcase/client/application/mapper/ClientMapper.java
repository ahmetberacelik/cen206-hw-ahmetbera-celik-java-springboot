package com.legalcase.client.application.mapper;

import com.legalcase.client.api.dto.request.ClientRequest;
import com.legalcase.client.api.dto.response.ClientResponse;
import com.legalcase.client.domain.entity.Client;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientMapper {
    
    public Client toEntity(ClientRequest request) {
        if (request == null) {
            return null;
        }
        
        Client client = new Client();
        client.setName(request.getName());
        client.setSurname(request.getSurname());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());
        client.setTaxId(request.getTaxId());
        client.setIdentityNumber(request.getIdentityNumber());
        client.setNotes(request.getNotes());
        client.setActive(true);
        
        return client;
    }
    
    public void updateEntityFromRequest(Client client, ClientRequest request) {
        if (request == null) {
            return;
        }
        
        client.setName(request.getName());
        client.setSurname(request.getSurname());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());
        client.setTaxId(request.getTaxId());
        client.setIdentityNumber(request.getIdentityNumber());
        client.setNotes(request.getNotes());
    }
    
    public ClientResponse toResponse(Client client) {
        if (client == null) {
            return null;
        }
        
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .surname(client.getSurname())
                .email(client.getEmail())
                .phoneNumber(client.getPhoneNumber())
                .address(client.getAddress())
                .taxId(client.getTaxId())
                .identityNumber(client.getIdentityNumber())
                .notes(client.getNotes())
                .active(client.isActive())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
    
    public List<ClientResponse> toResponseList(List<Client> clients) {
        if (clients == null) {
            return null;
        }
        
        return clients.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
} 