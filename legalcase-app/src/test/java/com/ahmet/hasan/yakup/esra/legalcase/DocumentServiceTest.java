package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import com.ahmet.hasan.yakup.esra.legalcase.repository.DocumentRepository;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private Logger logger;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        documentService = new DocumentService(documentRepository, caseRepository);
    }

    // Helper method to create a test document
    private Document createTestDocument() {
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        return new Document(1L, "Test Document", DocumentType.CONTRACT, testCase);
    }

    // Helper method to create a list of test documents
    private List<Document> createTestDocumentsList() {
        List<Document> documents = new ArrayList<>();
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);

        documents.add(new Document(1L, "Document 1", DocumentType.CONTRACT, testCase));
        documents.add(new Document(2L, "Document 2", DocumentType.EVIDENCE, testCase));
        documents.add(new Document(3L, "Document 3", DocumentType.PETITION, testCase));

        return documents;
    }

    @Test
    void createDocument_ValidDocument_ReturnsSuccess() {
        // Arrange
        Document testDocument = createTestDocument();
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        // Act
        ApiResponse<Document> response = documentService.createDocument(testDocument);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testDocument, response.getData());
        verify(documentRepository).save(testDocument);
    }

    @Test
    void createDocument_RepositoryException_ReturnsError() {
        // Arrange
        Document testDocument = createTestDocument();
        when(documentRepository.save(any(Document.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Document> response = documentService.createDocument(testDocument);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to create document"));
        verify(documentRepository).save(testDocument);
    }

    @Test
    void createDocumentWithContent_ValidParameters_ReturnsSuccess() {
        // Arrange
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        when(caseRepository.findById(1L)).thenReturn(Optional.of(testCase));

        Document savedDocument = new Document(1L, "New Document", DocumentType.CONTRACT, testCase);
        savedDocument.setContent("Test content");
        when(documentRepository.save(any(Document.class))).thenReturn(savedDocument);

        // Act
        ApiResponse<Document> response = documentService.createDocumentWithContent(
                1L, "New Document", DocumentType.CONTRACT, "Test content");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(savedDocument, response.getData());
        assertEquals("Test content", response.getData().getContent());
        verify(caseRepository).findById(1L);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void createDocumentWithContent_CaseNotFound_ReturnsError() {
        // Arrange
        when(caseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Document> response = documentService.createDocumentWithContent(
                999L, "New Document", DocumentType.CONTRACT, "Test content");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository).findById(999L);
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void createDocumentWithContent_RepositoryException_ReturnsError() {
        // Arrange
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        when(caseRepository.findById(1L)).thenReturn(Optional.of(testCase));
        when(documentRepository.save(any(Document.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Document> response = documentService.createDocumentWithContent(
                1L, "New Document", DocumentType.CONTRACT, "Test content");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to save document"));
        verify(caseRepository).findById(1L);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void getDocumentById_ValidId_ReturnsDocument() {
        // Arrange
        Document testDocument = createTestDocument();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        // Act
        ApiResponse<Document> response = documentService.getDocumentById(1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testDocument, response.getData());
        verify(documentRepository).findById(1L);
    }

    @Test
    void getDocumentById_InvalidId_ReturnsError() {
        // Arrange
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Document> response = documentService.getDocumentById(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Document not found"));
        verify(documentRepository).findById(999L);
    }

    @Test
    void getAllDocuments_ReturnsAllDocuments() {
        // Arrange
        List<Document> testDocuments = createTestDocumentsList();
        when(documentRepository.findAll()).thenReturn(testDocuments);

        // Act
        ApiResponse<List<Document>> response = documentService.getAllDocuments();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testDocuments, response.getData());
        assertEquals(3, response.getData().size());
        verify(documentRepository).findAll();
    }

    @Test
    void getAllDocuments_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(documentRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Document>> response = documentService.getAllDocuments();

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(documentRepository).findAll();
    }

    @Test
    void getDocumentsByCaseId_ValidCaseId_ReturnsDocuments() {
        // Arrange
        List<Document> testDocuments = createTestDocumentsList();
        when(caseRepository.existsById(1L)).thenReturn(true);
        when(documentRepository.findByCseId(1L)).thenReturn(testDocuments);

        // Act
        ApiResponse<List<Document>> response = documentService.getDocumentsByCaseId(1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testDocuments, response.getData());
        assertEquals(3, response.getData().size());
        verify(caseRepository).existsById(1L);
        verify(documentRepository).findByCseId(1L);
    }

    @Test
    void getDocumentsByCaseId_CaseNotFound_ReturnsError() {
        // Arrange
        when(caseRepository.existsById(999L)).thenReturn(false);

        // Act
        ApiResponse<List<Document>> response = documentService.getDocumentsByCaseId(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository).existsById(999L);
        verify(documentRepository, never()).findByCseId(999L);
    }

    @Test
    void getDocumentsByType_ValidType_ReturnsDocuments() {
        // Arrange
        List<Document> contractDocuments = List.of(
                new Document(1L, "Contract 1", DocumentType.CONTRACT),
                new Document(2L, "Contract 2", DocumentType.CONTRACT)
        );
        when(documentRepository.findByType(DocumentType.CONTRACT)).thenReturn(contractDocuments);

        // Act
        ApiResponse<List<Document>> response = documentService.getDocumentsByType(DocumentType.CONTRACT);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(contractDocuments, response.getData());
        assertEquals(2, response.getData().size());
        verify(documentRepository).findByType(DocumentType.CONTRACT);
    }

    @Test
    void getDocumentsByType_EmptyResult_ReturnsEmptyList() {
        // Arrange
        when(documentRepository.findByType(DocumentType.COURT_ORDER)).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Document>> response = documentService.getDocumentsByType(DocumentType.COURT_ORDER);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(documentRepository).findByType(DocumentType.COURT_ORDER);
    }

    @Test
    void searchDocumentsByTitle_ValidKeyword_ReturnsMatchingDocuments() {
        // Arrange
        List<Document> matchingDocuments = List.of(
                new Document(1L, "Contract Agreement", DocumentType.CONTRACT),
                new Document(2L, "Service Contract", DocumentType.CONTRACT)
        );
        when(documentRepository.findByTitleContainingIgnoreCase("Contract")).thenReturn(matchingDocuments);

        // Act
        ApiResponse<List<Document>> response = documentService.searchDocumentsByTitle("Contract");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(matchingDocuments, response.getData());
        assertEquals(2, response.getData().size());
        verify(documentRepository).findByTitleContainingIgnoreCase("Contract");
    }

    @Test
    void searchDocumentsByTitle_NoMatches_ReturnsEmptyList() {
        // Arrange
        when(documentRepository.findByTitleContainingIgnoreCase("NonExistent")).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Document>> response = documentService.searchDocumentsByTitle("NonExistent");

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(documentRepository).findByTitleContainingIgnoreCase("NonExistent");
    }

    @Test
    void updateDocument_ValidIdAndDocument_ReturnsUpdatedDocument() {
        // Arrange
        Document existingDocument = createTestDocument();
        Document updatedDocument = new Document(1L, "Updated Title", DocumentType.EVIDENCE);
        updatedDocument.setContent("Updated content");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> {
            Document savedDoc = invocation.getArgument(0);
            return savedDoc; // Return the saved document
        });

        // Act
        ApiResponse<Document> response = documentService.updateDocument(1L, updatedDocument);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Updated Title", response.getData().getTitle());
        assertEquals(DocumentType.EVIDENCE, response.getData().getType());
        assertEquals("Updated content", response.getData().getContent());
        verify(documentRepository).findById(1L);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void updateDocument_DocumentNotFound_ReturnsError() {
        // Arrange
        Document updatedDocument = new Document(999L, "Updated Title", DocumentType.EVIDENCE);
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Document> response = documentService.updateDocument(999L, updatedDocument);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Document not found"));
        verify(documentRepository).findById(999L);
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void updateDocument_RepositoryException_ReturnsError() {
        // Arrange
        Document existingDocument = createTestDocument();
        Document updatedDocument = new Document(1L, "Updated Title", DocumentType.EVIDENCE);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(documentRepository.save(any(Document.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Document> response = documentService.updateDocument(1L, updatedDocument);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to update document"));
        verify(documentRepository).findById(1L);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void updateDocument_PreservesCase_WhenNotProvided() {
        // Arrange
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        Document existingDocument = new Document(1L, "Original Title", DocumentType.CONTRACT, testCase);

        Document updatedDocument = new Document(1L, "Updated Title", DocumentType.EVIDENCE);
        updatedDocument.setCse(null); // No case provided in the update

        when(documentRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApiResponse<Document> response = documentService.updateDocument(1L, updatedDocument);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Updated Title", response.getData().getTitle());
        assertEquals(DocumentType.EVIDENCE, response.getData().getType());
        assertEquals(testCase, response.getData().getCse()); // Case should be preserved
        verify(documentRepository).findById(1L);
        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void deleteDocument_ValidId_ReturnsSuccess() {
        // Arrange
        Document testDocument = createTestDocument();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        doNothing().when(documentRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = documentService.deleteDocument(1L);

        // Assert
        assertTrue(response.isSuccess());
        verify(documentRepository).findById(1L);
        verify(documentRepository).deleteById(1L);
    }

    @Test
    void deleteDocument_DocumentNotFound_ReturnsError() {
        // Arrange
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Void> response = documentService.deleteDocument(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Document not found"));
        verify(documentRepository).findById(999L);
        verify(documentRepository, never()).deleteById(any());
    }

    @Test
    void deleteDocument_RepositoryException_ReturnsError() {
        // Arrange
        Document testDocument = createTestDocument();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        doThrow(new RuntimeException("Database error")).when(documentRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = documentService.deleteDocument(1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to delete document"));
        verify(documentRepository).findById(1L);
        verify(documentRepository).deleteById(1L);
    }
}