package com.ahmet.hasan.yakup.esra.legalcase.model;

/**
 * @brief Model class that holds document information.
 * @author Team
 * @date March 2025
 */
public class Document extends BaseEntity {
    private String title;
    private DocumentType type;
    private Case cse;

    /**
     * @brief Default constructor
     */
    public Document() {
        super();
    }

    /**
     * @brief Parameterized constructor
     * @param id Document ID
     * @param title Document title
     * @param type Document type
     */
    public Document(Long id, String title, DocumentType type) {
        super(id);
        this.title = title;
        this.type = type;
    }

    /**
     * @brief Parameterized constructor with case
     * @param id Document ID
     * @param title Document title
     * @param type Document type
     * @param cse Associated case
     */
    public Document(Long id, String title, DocumentType type, Case cse) {
        super(id);
        this.title = title;
        this.type = type;
        this.cse = cse;
    }

    /**
     * @brief Get document title
     * @return Document title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @brief Set document title
     * @param title Document title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @brief Get document type
     * @return Document type
     */
    public DocumentType getType() {
        return type;
    }

    /**
     * @brief Set document type
     * @param type Document type to set
     */
    public void setType(DocumentType type) {
        this.type = type;
    }

    /**
     * @brief Get case associated with the document
     * @return Associated case
     */
    public Case getCase() {
        return cse;
    }

    /**
     * @brief Set case associated with the document
     * @param cse Case to set
     */
    public void setCase(Case cse) {
        this.cse = cse;
    }

    /**
     * @brief String representation of the document
     * @return String representing the document object
     */
    @Override
    public String toString() {
        return "Document{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", case=" + (cse != null ? cse.getCaseNumber() : "none") +
                '}';
    }
}