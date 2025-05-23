package com.ahmet.hasan.yakup.esra.legalcase.api;

import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IDocumentService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    private final IDocumentService documentService;

    @Autowired
    public DocumentController(IDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Document>> createDocument(@RequestBody Document document) {
        logger.info("REST request to create a new document");
        ApiResponse<Document> response = documentService.createDocument(document);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    @PostMapping("/with-content")
    public ResponseEntity<ApiResponse<Document>> createDocumentWithContent(
            @RequestParam("caseId") Long caseId,
            @RequestParam("title") String title,
            @RequestParam("type") DocumentType type,
            @RequestParam("content") String content) {

        logger.info("REST request to create a new document with content for case ID: {}", caseId);
        ApiResponse<Document> response = documentService.createDocumentWithContent(caseId, title, type, content);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.CREATED : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Document>> getDocumentById(@PathVariable Long id) {
        logger.info("REST request to get document by ID: {}", id);
        ApiResponse<Document> response = documentService.getDocumentById(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Document>>> getAllDocuments() {
        logger.info("REST request to get all documents");
        ApiResponse<List<Document>> response = documentService.getAllDocuments();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByCaseId(@PathVariable Long caseId) {
        logger.info("REST request to get documents by case ID: {}", caseId);
        ApiResponse<List<Document>> response = documentService.getDocumentsByCaseId(caseId);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<Document>>> getDocumentsByType(@PathVariable DocumentType type) {
        logger.info("REST request to get documents by type: {}", type);
        ApiResponse<List<Document>> response = documentService.getDocumentsByType(type);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Document>>> searchDocuments(@RequestParam String keyword) {
        logger.info("REST request to search documents by title: {}", keyword);
        ApiResponse<List<Document>> response = documentService.searchDocumentsByTitle(keyword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Document>> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        logger.info("REST request to update document with ID: {}", id);
        if (document.getId() != null && !document.getId().equals(id)) {
            return new ResponseEntity<>(
                    ApiResponse.error("ID in the URL does not match the ID in the request body", HttpStatus.BAD_REQUEST.value()),
                    HttpStatus.BAD_REQUEST);
        }
        ApiResponse<Document> response = documentService.updateDocument(id, document);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.OK : HttpStatus.valueOf(response.getErrorCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable Long id) {
        logger.info("REST request to delete document with ID: {}", id);
        ApiResponse<Void> response = documentService.deleteDocument(id);
        return new ResponseEntity<>(response,
                response.isSuccess() ? HttpStatus.NO_CONTENT : HttpStatus.valueOf(response.getErrorCode()));
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<ApiResponse<String>> getDocumentContent(@PathVariable Long id) {
        logger.info("REST request to get document content with ID: {}", id);

        // Get the document
        ApiResponse<Document> documentResponse = documentService.getDocumentById(id);
        if (!documentResponse.isSuccess()) {
            return new ResponseEntity<>(
                    ApiResponse.error("Document not found", documentResponse.getErrorCode()),
                    HttpStatus.valueOf(documentResponse.getErrorCode()));
        }

        // Get the content from the document
        Document document = documentResponse.getData();
        String content = document.getContent();

        if (content == null) {
            return new ResponseEntity<>(
                    ApiResponse.error("Document has no content", HttpStatus.NOT_FOUND.value()),
                    HttpStatus.NOT_FOUND);
        }

        // Return the content
        return ResponseEntity.ok(ApiResponse.success(content));
    }
}