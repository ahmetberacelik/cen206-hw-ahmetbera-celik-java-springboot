package com.ahmet.hasan.yakup.esra.legalcase.model;

import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document extends BaseEntity {
    public Document() {
        super();
    }

    public Document(Long id, String title, DocumentType type) {
        super(id);
        this.title = title;
        this.type = type;
    }
    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @ManyToOne
    @JoinColumn(name = "case_id")
    private Case cse;

    // Add file path for document storage
    @Column(name = "file_path")
    private String filePath;

    // Add file content type
    @Column(name = "content_type")
    private String contentType;

    // Add file size
    @Column(name = "file_size")
    private Long fileSize;


    // Parametreli constructor (case ile)
    public Document(Long id, String title, DocumentType type, Case cse) {
        super(id);
        this.title = title;
        this.type = type;
        this.cse = cse;
    }
}