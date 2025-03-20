package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "hearings")
@Getter
@Setter
public class Hearing extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private Case cse;

    @Column(name = "hearing_date", nullable = false)
    private LocalDateTime hearingDate;

    @Column(nullable = false)
    private String judge;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HearingStatus status;

    // Add location for the hearing
    @Column
    private String location;

    // Add notes about the hearing
    @Column(length = 1000)
    private String notes;

    // Constructors remain the same

    // reschedule method remains the same
}