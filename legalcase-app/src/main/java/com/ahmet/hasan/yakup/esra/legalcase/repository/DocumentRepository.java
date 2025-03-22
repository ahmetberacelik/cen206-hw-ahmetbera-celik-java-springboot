package com.ahmet.hasan.yakup.esra.legalcase.repository;

import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCseId(Long caseId);
    List<Document> findByType(DocumentType type);
    List<Document> findByTitleContainingIgnoreCase(String title);

    // Dosya içeriğine göre arama için
    List<Document> findByContentTypeContaining(String contentType);

    // Dosya boyutuna göre filtreleme için
    List<Document> findByFileSizeGreaterThan(Long minSize);
    List<Document> findByFileSizeLessThan(Long maxSize);
}