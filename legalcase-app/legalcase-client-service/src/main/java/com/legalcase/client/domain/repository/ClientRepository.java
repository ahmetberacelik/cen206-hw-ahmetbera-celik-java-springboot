package com.legalcase.client.domain.repository;

import com.legalcase.client.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByEmail(String email);
    
    List<Client> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
    
    List<Client> findByActive(boolean active);
    
    boolean existsByEmail(String email);
    
    boolean existsByIdentityNumber(String identityNumber);
    
    boolean existsByTaxId(String taxId);
} 