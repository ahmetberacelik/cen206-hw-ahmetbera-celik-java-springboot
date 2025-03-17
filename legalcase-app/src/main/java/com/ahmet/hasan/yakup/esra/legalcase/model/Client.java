package com.ahmet.hasan.yakup.esra.legalcase.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Model class that holds client information.
 * @author Team
 * @date March 2025
 */
public class Client extends BaseEntity {
    private String name;
    private String surname;
    private String email;
    private List<Case> cases;

    /**
     * @brief Default constructor
     */
    public Client() {
        super();
        this.cases = new ArrayList<>();
    }

    /**
     * @brief Parameterized constructor
     * @param id Client ID
     * @param name Client name
     * @param surname Client surname
     * @param email Client email address
     */
    public Client(Long id, String name, String surname, String email) {
        super(id);
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.cases = new ArrayList<>();
    }

    /**
     * @brief Get client name
     * @return Client name
     */
    public String getName() {
        return name;
    }

    /**
     * @brief Set client name
     * @param name Client name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @brief Get client surname
     * @return Client surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @brief Set client surname
     * @param surname Client surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @brief Get email address
     * @return Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @brief Set email address
     * @param email Email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @brief Get client's cases
     * @return List of cases
     */
    public List<Case> getCases() {
        return cases;
    }

    /**
     * @brief Set list of cases
     * @param cases List of cases to set
     */
    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    /**
     * @brief Add a case to the client
     * @param cse Case to add
     */
    public void addCase(Case cse) {
        if (!this.cases.contains(cse)) {
            this.cases.add(cse);
            // Maintain bidirectional relationship
            if (!cse.getClients().contains(this)) {
                cse.getClients().add(this);
            }
        }
    }

    /**
     * @brief Remove a case from the client
     * @param cse Case to remove
     */
    public void removeCase(Case cse) {
        if (this.cases.contains(cse)) {
            this.cases.remove(cse);
            // Maintain bidirectional relationship
            if (cse.getClients().contains(this)) {
                cse.getClients().remove(this);
            }
        }
    }

    /**
     * @brief String representation of the client
     * @return String representing the client object
     */
    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", caseCount=" + cases.size() +
                '}';
    }
}