package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    ApiResponse<Document> createDocument(Document document);
    ApiResponse<Document> uploadDocument(Long caseId, String title, DocumentType type, MultipartFile file);
    ApiResponse<Document> getDocumentById(Long id);
    ApiResponse<List<Document>> getAllDocuments();
    ApiResponse<List<Document>> getDocumentsByCaseId(Long caseId);
    ApiResponse<List<Document>> getDocumentsByType(DocumentType type);
    ApiResponse<List<Document>> searchDocumentsByTitle(String keyword);
    ApiResponse<Document> updateDocument(Long id, Document document);
    ApiResponse<Void> deleteDocument(Long id);
    ApiResponse<byte[]> getDocumentContent(Long id);
}