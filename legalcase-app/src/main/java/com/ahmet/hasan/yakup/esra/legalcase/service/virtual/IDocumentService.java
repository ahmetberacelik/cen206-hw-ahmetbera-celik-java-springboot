package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.List;

public interface IDocumentService {
    /**
     * Create a document with basic information
     */
    ApiResponse<Document> createDocument(Document document);

    /**
     * Create a document with case, title, type and content
     */
    ApiResponse<Document> createDocumentWithContent(Long caseId, String title, DocumentType type, String content);

    /**
     * Get document by ID
     */
    ApiResponse<Document> getDocumentById(Long id);

    /**
     * Get all documents
     */
    ApiResponse<List<Document>> getAllDocuments();

    /**
     * Get documents by case ID
     */
    ApiResponse<List<Document>> getDocumentsByCaseId(Long caseId);

    /**
     * Get documents by type
     */
    ApiResponse<List<Document>> getDocumentsByType(DocumentType type);

    /**
     * Search documents by title
     */
    ApiResponse<List<Document>> searchDocumentsByTitle(String keyword);

    /**
     * Update document
     */
    ApiResponse<Document> updateDocument(Long id, Document document);

    /**
     * Delete document
     */
    ApiResponse<Void> deleteDocument(Long id);
}