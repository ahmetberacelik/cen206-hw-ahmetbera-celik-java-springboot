package com.ahmet.hasan.yakup.esra.legalcase.repository;

import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    List<Client> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);
}