package com.ahmet.hasan.yakup.esra.legalcase.service.virtual;

import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    Document createDocument(Document document);
    Document uploadDocument(Long caseId, String title, DocumentType type, MultipartFile file) throws IOException;
    Optional<Document> getDocumentById(Long id);
    List<Document> getAllDocuments();
    List<Document> getDocumentsByCaseId(Long caseId);
    List<Document> getDocumentsByType(DocumentType type);
    List<Document> searchDocumentsByTitle(String keyword);
    Document updateDocument(Long id, Document document);
    void deleteDocument(Long id);
    byte[] getDocumentContent(Long id) throws IOException;
}