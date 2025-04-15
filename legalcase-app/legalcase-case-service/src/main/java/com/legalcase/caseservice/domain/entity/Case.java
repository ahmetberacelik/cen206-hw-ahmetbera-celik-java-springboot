package com.legalcase.caseservice.domain.entity;

import com.legalcase.caseservice.domain.valueobject.CaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a legal case
 */
@Entity
@Table(name = "cases")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Case {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "case_number", unique = true, nullable = false)
    private String caseNumber;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;
    
    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;
    
    @Column(name = "close_date")
    private LocalDate closeDate;
    
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    
    @Column(name = "assigned_user_id")
    private Long assignedUserId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Updates the case details
     * 
     * @param title new title
     * @param description new description
     */
    public void updateDetails(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    /**
     * Assigns a user to this case
     * 
     * @param userId ID of the user to assign
     */
    public void assignUser(Long userId) {
        this.assignedUserId = userId;
    }
    
    /**
     * Updates the status of this case
     * 
     * @param status new status
     */
    public void updateStatus(CaseStatus status) {
        this.status = status;
    }
    
    /**
     * Closes this case with the given date
     * 
     * @param closeDate the date the case was closed
     */
    public void close(LocalDate closeDate) {
        this.status = CaseStatus.CLOSED;
        this.closeDate = closeDate;
    }
} 