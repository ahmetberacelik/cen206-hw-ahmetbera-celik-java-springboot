package com.ahmet.hasan.yakup.esra.legalcase.model;

import java.time.LocalDateTime;

/**
 * @brief Base entity class. Contains basic properties for all model classes.
 * @author Team
 * @date March 2025
 */
public abstract class BaseEntity {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * @brief Default constructor
     */
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * @brief Constructor with specific id
     * @param id Entity id
     */
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    /**
     * @brief Get entity id
     * @return Entity id
     */
    public Long getId() {
        return id;
    }

    /**
     * @brief Set entity id
     * @param id Entity id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @brief Get creation time
     * @return Creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @brief Set creation time
     * @param createdAt Creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @brief Get last update time
     * @return Last update time
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @brief Set last update time
     * @param updatedAt Last update time to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @brief Method to be called before updating the entity
     */
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * @brief Check if this entity equals another object
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    /**
     * @brief Generate hash code for the entity
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}