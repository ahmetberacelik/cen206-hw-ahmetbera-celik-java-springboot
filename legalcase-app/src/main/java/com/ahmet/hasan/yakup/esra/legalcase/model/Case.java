package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
@Getter
@Setter
public class Case extends BaseEntity {
    public Case() {
        super();
        this.clients = new ArrayList<>();
        this.hearings = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.status = CaseStatus.NEW;
    }
    // Temel constructor
    public Case(Long id, String caseNumber, String title, CaseType type) {
        super(id);
        this.caseNumber = caseNumber;
        this.title = title;
        this.type = type;
        this.status = CaseStatus.NEW;
    }

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


    // Helper metodlar
    public void addClient(Client client) {
        if (!this.clients.contains(client)) {
            this.clients.add(client);
            if (!client.getCases().contains(this)) {
                client.getCases().add(this);
            }
        }
    }

    public void removeClient(Client client) {
        if (this.clients.contains(client)) {
            this.clients.remove(client);
            if (client.getCases().contains(this)) {
                client.getCases().remove(this);
            }
        }
    }

    public void addHearing(Hearing hearing) {
        if (!this.hearings.contains(hearing)) {
            this.hearings.add(hearing);
            hearing.setCse(this);
        }
    }

    public void addDocument(Document document) {
        if (!this.documents.contains(document)) {
            this.documents.add(document);
            document.setCse(this);
        }
    }
}