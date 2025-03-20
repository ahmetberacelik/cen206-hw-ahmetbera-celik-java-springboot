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

    // Parametresiz constructor
    public Hearing() {
        super();
        this.status = HearingStatus.SCHEDULED;
    }

    // Parametreli constructor
    public Hearing(Long id, Case cse, LocalDateTime hearingDate, String judge) {
        super(id);
        this.cse = cse;
        this.hearingDate = hearingDate;
        this.judge = judge;
        this.status = HearingStatus.SCHEDULED;
    }

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
}