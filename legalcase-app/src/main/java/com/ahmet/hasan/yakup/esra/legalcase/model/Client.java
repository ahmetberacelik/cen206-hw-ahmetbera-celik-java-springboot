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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(unique = true)
    private String email;

    @ManyToMany(mappedBy = "clients")
    private List<Case> cases = new ArrayList<>();

    // Constructors remain the same

    // addCase, removeCase methods remain the same
}