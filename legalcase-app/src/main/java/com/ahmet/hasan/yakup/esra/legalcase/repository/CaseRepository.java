package com.ahmet.hasan.yakup.esra.legalcase.repository;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    Optional<Case> findByCaseNumber(String caseNumber);

    List<Case> findByStatus(CaseStatus status);

    List<Case> findByType(CaseType type);

    @Query("SELECT c FROM Case c JOIN c.clients cli WHERE cli.id = :clientId")
    List<Case> findByClientId(@Param("clientId") Long clientId);

    List<Case> findByTitleContainingIgnoreCase(String title);
}