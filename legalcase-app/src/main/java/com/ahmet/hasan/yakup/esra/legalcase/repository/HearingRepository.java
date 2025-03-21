package com.ahmet.hasan.yakup.esra.legalcase.repository;

import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, Long> {

    List<Hearing> findByCseId(Long caseId);

    List<Hearing> findByStatus(HearingStatus status);

    List<Hearing> findByHearingDateBetween(LocalDateTime start, LocalDateTime end);

    List<Hearing> findByHearingDateAfterAndStatusNot(LocalDateTime date, HearingStatus status);
}