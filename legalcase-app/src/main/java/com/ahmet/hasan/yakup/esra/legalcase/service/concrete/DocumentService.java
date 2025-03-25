package com.ahmet.hasan.yakup.esra.legalcase.service.concrete;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import com.ahmet.hasan.yakup.esra.legalcase.repository.DocumentRepository;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IDocumentService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentService implements IDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final CaseRepository caseRepository;

    @Autowired
    public DocumentService(DocumentRepository documentRepository, CaseRepository caseRepository) {
        this.documentRepository = documentRepository;
        this.caseRepository = caseRepository;
    }

    @Override
    public ApiResponse<Document> createDocument(Document document) {
        logger.info("Creating new document: {}", document.getTitle());
        try {
            Document savedDocument = documentRepository.save(document);
            return ApiResponse.success(savedDocument);
        } catch (Exception e) {
            logger.error("Error creating document: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to create document: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Document> createDocumentWithContent(Long caseId, String title, DocumentType type, String content) {
        logger.info("Creating new document with content for case ID: {}", caseId);

        // Check Case
        Optional<Case> caseOptional = caseRepository.findById(caseId);
        if (caseOptional.isEmpty()) {
            return ApiResponse.error("Case not found with ID: " + caseId,
                    HttpStatus.NOT_FOUND.value());
        }

        try {
            // Create the Document object
            Document document = new Document();
            document.setTitle(title);
            document.setType(type);
            document.setCse(caseOptional.get());
            document.setContent(content);

            Document savedDocument = documentRepository.save(document);
            return ApiResponse.success(savedDocument);
        } catch (Exception e) {
            logger.error("Error saving document: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to save document: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Document> getDocumentById(Long id) {
        logger.info("Getting document by ID: {}", id);
        Optional<Document> documentOptional = documentRepository.findById(id);
        if (documentOptional.isPresent()) {
            return ApiResponse.success(documentOptional.get());
        } else {
            return ApiResponse.error("Document not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Document>> getAllDocuments() {
        logger.info("Getting all documents");
        List<Document> documents = documentRepository.findAll();
        return ApiResponse.success(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Document>> getDocumentsByCaseId(Long caseId) {
        logger.info("Getting documents by case ID: {}", caseId);
        // First check the existence of case
        if (!caseRepository.existsById(caseId)) {
            return ApiResponse.error("Case not found with ID: " + caseId,
                    HttpStatus.NOT_FOUND.value());
        }
        List<Document> documents = documentRepository.findByCseId(caseId);
        return ApiResponse.success(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Document>> getDocumentsByType(DocumentType type) {
        logger.info("Getting documents by type: {}", type);
        List<Document> documents = documentRepository.findByType(type);
        return ApiResponse.success(documents);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<Document>> searchDocumentsByTitle(String keyword) {
        logger.info("Searching documents by title containing: {}", keyword);
        List<Document> documents = documentRepository.findByTitleContainingIgnoreCase(keyword);
        return ApiResponse.success(documents);
    }

    @Override
    public ApiResponse<Document> updateDocument(Long id, Document document) {
        logger.info("Updating document with ID: {}", id);

        Optional<Document> existingDocument = documentRepository.findById(id);
        if (existingDocument.isEmpty()) {
            return ApiResponse.error("Document not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }

        try {
            Document documentToUpdate = existingDocument.get();
            documentToUpdate.setTitle(document.getTitle());
            documentToUpdate.setType(document.getType());

            // Update content if provided
            if (document.getContent() != null) {
                documentToUpdate.setContent(document.getContent());
            }

            // Preserve other fields if case is not updated
            if (document.getCse() != null) {
                documentToUpdate.setCse(document.getCse());
            }

            Document updatedDocument = documentRepository.save(documentToUpdate);
            return ApiResponse.success(updatedDocument);
        } catch (Exception e) {
            logger.error("Error updating document: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to update document: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ApiResponse<Void> deleteDocument(Long id) {
        logger.info("Deleting document with ID: {}", id);

        Optional<Document> documentOptional = documentRepository.findById(id);
        if (documentOptional.isEmpty()) {
            return ApiResponse.error("Document not found with ID: " + id,
                    HttpStatus.NOT_FOUND.value());
        }

        try {
            // Delete from database
            documentRepository.deleteById(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage(), e);
            return ApiResponse.error("Failed to delete document: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}