package com.ahmet.hasan.yakup.esra.legalcase.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client extends BaseEntity {
    public Client() {
        super();
        this.cases = new ArrayList<>();
    }

    public Client(Long id, String name, String surname, String email) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.cases = new ArrayList<>();
    }
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true)
    private String email;

    @ManyToMany(mappedBy = "clients", fetch = FetchType.EAGER)
    private List<Case> cases = new ArrayList<>();

    //Helper methods
    public void addCase(Case cse) {
        if (!this.cases.contains(cse)) {
            this.cases.add(cse);
            if (!cse.getClients().contains(this)) {
                cse.getClients().add(this);
            }
        }
    }

    public void removeCase(Case cse) {
        if (this.cases.contains(cse)) {
            this.cases.remove(cse);
            if (cse.getClients().contains(this)) {
                cse.getClients().remove(this);
            }
        }
    }
}