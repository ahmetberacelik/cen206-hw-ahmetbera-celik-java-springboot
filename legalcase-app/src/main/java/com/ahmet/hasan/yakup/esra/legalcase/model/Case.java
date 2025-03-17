package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Model class that holds case information.
 * @author Team
 * @date March 2025
 */
public class Case extends BaseEntity {
    private String caseNumber;
    private String title;
    private CaseType type;
    private String description;
    private CaseStatus status;
    private List<Client> clients;
    private List<Hearing> hearings;
    private List<Document> documents;

    /**
     * @brief Default constructor
     */
    public Case() {
        super();
        this.clients = new ArrayList<>();
        this.hearings = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.status = CaseStatus.NEW;
    }

    /**
     * @brief Parameterized constructor
     * @param id Case ID
     * @param caseNumber Case number
     * @param title Case title
     * @param type Case type
     */
    public Case(Long id, String caseNumber, String title, CaseType type) {
        super(id);
        this.caseNumber = caseNumber;
        this.title = title;
        this.type = type;
        this.clients = new ArrayList<>();
        this.hearings = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.status = CaseStatus.NEW;
    }

    /**
     * @brief Get case number
     * @return Case number
     */
    public String getCaseNumber() {
        return caseNumber;
    }

    /**
     * @brief Set case number
     * @param caseNumber Case number to set
     */
    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    /**
     * @brief Get case title
     * @return Case title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @brief Set case title
     * @param title Case title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @brief Get case type
     * @return Case type
     */
    public CaseType getType() {
        return type;
    }

    /**
     * @brief Set case type
     * @param type Case type to set
     */
    public void setType(CaseType type) {
        this.type = type;
    }

    /**
     * @brief Get case description
     * @return Case description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @brief Set case description
     * @param description Case description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @brief Get case status
     * @return Case status
     */
    public CaseStatus getStatus() {
        return status;
    }

    /**
     * @brief Set case status
     * @param status Case status to set
     */
    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    /**
     * @brief Get clients associated with the case
     * @return List of clients
     */
    public List<Client> getClients() {
        return clients;
    }

    /**
     * @brief Set list of clients
     * @param clients List of clients to set
     */
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    /**
     * @brief Get case hearings
     * @return List of hearings
     */
    public List<Hearing> getHearings() {
        return hearings;
    }

    /**
     * @brief Set list of hearings
     * @param hearings List of hearings to set
     */
    public void setHearings(List<Hearing> hearings) {
        this.hearings = hearings;
    }

    /**
     * @brief Get case documents
     * @return List of documents
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * @brief Set list of documents
     * @param documents List of documents to set
     */
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    /**
     * @brief Add a client to the case
     * @param client Client to add
     */
    public void addClient(Client client) {
        if (!this.clients.contains(client)) {
            this.clients.add(client);
            client.addCase(this);
        }
    }

    /**
     * @brief Remove a client from the case
     * @param client Client to remove
     */
    public void removeClient(Client client) {
        if (this.clients.contains(client)) {
            this.clients.remove(client);
            client.removeCase(this);
        }
    }

    /**
     * @brief Add a hearing to the case
     * @param hearing Hearing to add
     */
    public void addHearing(Hearing hearing) {
        if (!this.hearings.contains(hearing)) {
            this.hearings.add(hearing);
            hearing.setCase(this);
        }
    }

    /**
     * @brief Add a document to the case
     * @param document Document to add
     */
    public void addDocument(Document document) {
        if (!this.documents.contains(document)) {
            this.documents.add(document);
            document.setCase(this);
        }
    }

    /**
     * @brief String representation of the case
     * @return String representing the case object
     */
    @Override
    public String toString() {
        return "Case{" +
                "id=" + getId() +
                ", caseNumber='" + caseNumber + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", clientCount=" + clients.size() +
                ", hearingCount=" + hearings.size() +
                ", documentCount=" + documents.size() +
                '}';
    }
}