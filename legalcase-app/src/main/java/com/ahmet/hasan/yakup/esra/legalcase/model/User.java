package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    public User() {
        super();
        this.enabled = true;
    }

    public User(Long id, String username, String email, String name, String surname, UserRole role) {
        super(id);
        this.username = username;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.enabled = true;
    }
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // Add keycloak ID for integration
    @Column(name = "keycloak_id")
    private String keycloakId;

    // Add whether account is enabled
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    // Constructors remain the same

    // authenticate, hasRole methods remain the same
}