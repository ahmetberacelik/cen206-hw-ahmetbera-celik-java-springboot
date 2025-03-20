package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
@Getter
@Setter
public class Case extends BaseEntity {

    @Column(name = "case_number", unique = true)
    private String caseNumber;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseType type;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;

    @ManyToMany
    @JoinTable(
            name = "case_client",
            joinColumns = @JoinColumn(name = "case_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "cse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hearing> hearings = new ArrayList<>();

    @OneToMany(mappedBy = "cse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

    // Constructors remain the same

    // addClient, removeClient, addHearing, addDocument methods remain the same
}