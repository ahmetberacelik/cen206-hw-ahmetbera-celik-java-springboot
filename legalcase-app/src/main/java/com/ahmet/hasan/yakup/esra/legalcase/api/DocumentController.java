package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        logger.info("REST request to create a new document");
        Document createdDocument = documentService.createDocument(document);
        return new ResponseEntity<>(createdDocument, HttpStatus.CREATED);
    }

    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("caseId") Long caseId,
            @RequestParam("title") String title,
            @RequestParam("type") DocumentType type,
            @RequestParam("file") MultipartFile file) {

        logger.info("REST request to upload a new document for case ID: {}", caseId);
        try {
            Document document = documentService.uploadDocument(caseId, title, type, file);
            return new ResponseEntity<>(document, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("Error uploading document: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        logger.info("REST request to get document by ID: {}", id);
        return documentService.getDocumentById(id)
                .map(document -> new ResponseEntity<>(document, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        logger.info("REST request to get all documents");
        List<Document> documents = documentService.getAllDocuments();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<Document>> getDocumentsByCaseId(@PathVariable Long caseId) {
        logger.info("REST request to get documents by case ID: {}", caseId);
        List<Document> documents = documentService.getDocumentsByCaseId(caseId);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Document>> getDocumentsByType(@PathVariable DocumentType type) {
        logger.info("REST request to get documents by type: {}", type);
        List<Document> documents = documentService.getDocumentsByType(type);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Document>> searchDocuments(@RequestParam String keyword) {
        logger.info("REST request to search documents by title: {}", keyword);
        List<Document> documents = documentService.searchDocumentsByTitle(keyword);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        logger.info("REST request to update document with ID: {}", id);
        try {
            Document updatedDocument = documentService.updateDocument(id, document);
            return new ResponseEntity<>(updatedDocument, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        logger.info("REST request to delete document with ID: {}", id);
        documentService.deleteDocument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getDocumentContent(@PathVariable Long id) {
        logger.info("REST request to download document content with ID: {}", id);
        try {
            Document document = documentService.getDocumentById(id).orElse(null);
            if (document == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            byte[] content = documentService.getDocumentContent(id);
            ByteArrayResource resource = new ByteArrayResource(content);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getTitle() + "\"")
                    .body(resource);
        } catch (IOException e) {
            logger.error("Error downloading document: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}