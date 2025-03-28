package com.ahmet.hasan.yakup.esra.legalcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ahmet.hasan.yakup.esra.legalcase.api.DocumentController;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IDocumentService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

/**
 * Test for DocumentController using Mockito
 * This isolates tests from Spring context loading issues
 */
@ExtendWith(MockitoExtension.class)
public class DocumentControllerTest {

    @Mock
    private IDocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private Document testDocument;
    private Case testCase;
    private List<Document> testDocumentList;
    private ApiResponse<Document> successResponse;
    private ApiResponse<Document> errorResponse;
    private ApiResponse<List<Document>> listSuccessResponse;
    private ApiResponse<String> contentSuccessResponse;

    @BeforeEach
    public void setup() {
        // Setup test case
        testCase = new Case();
        testCase.setId(1L);
        testCase.setTitle("Test Case");

        // Setup test document
        testDocument = new Document();
        testDocument.setId(1L);
        testDocument.setTitle("Test Document");
        testDocument.setType(DocumentType.EVIDENCE);
        testDocument.setCse(testCase);
        testDocument.setContent("This is test content");

        // Setup test list
        testDocumentList = new ArrayList<>();
        testDocumentList.add(testDocument);

        // Create response objects
        successResponse = ApiResponse.success(testDocument);
        errorResponse = ApiResponse.error("Test error message", HttpStatus.BAD_REQUEST.value());
        listSuccessResponse = ApiResponse.success(testDocumentList);
        contentSuccessResponse = ApiResponse.success("This is test content");
    }

    @Test
    public void testCreateDocument_Success() {
        // Arrange
        when(documentService.createDocument(any(Document.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Document>> response = documentController.createDocument(testDocument);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocument, response.getBody().getData());

        // Verify service method was called
        verify(documentService).createDocument(any(Document.class));
    }

    @Test
    public void testCreateDocument_Failure() {
        // Arrange
        when(documentService.createDocument(any(Document.class))).thenReturn(errorResponse);

        // Act
        ResponseEntity<ApiResponse<Document>> response = documentController.createDocument(testDocument);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorResponse.getErrorMessages().get(0), response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(documentService).createDocument(any(Document.class));
    }

    @Test
    public void testCreateDocumentWithContent_Success() {
        // Arrange
        when(documentService.createDocumentWithContent(anyLong(), anyString(), any(DocumentType.class), anyString()))
                .thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Document>> response = documentController.createDocumentWithContent(
                1L, "Test Document", DocumentType.EVIDENCE, "This is test content");

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocument, response.getBody().getData());

        // Verify service method was called
        verify(documentService).createDocumentWithContent(1L, "Test Document", DocumentType.EVIDENCE, "This is test content");
    }

    @Test
    public void testGetDocumentById_Success() {
        // Arrange
        when(documentService.getDocumentById(anyLong())).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Document>> response = documentController.getDocumentById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocument, response.getBody().getData());

        // Verify service method was called
        verify(documentService).getDocumentById(1L);
    }

    @Test
    public void testGetDocumentById_NotFound() {
        // Arrange
        when(documentService.getDocumentById(anyLong())).thenReturn(
                ApiResponse.error("Document not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Document>> response = documentController.getDocumentById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Document not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(documentService).getDocumentById(1L);
    }

    @Test
    public void testGetAllDocuments() {
        // Arrange
        when(documentService.getAllDocuments()).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Document>>> response = documentController.getAllDocuments();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocumentList, response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());

        // Verify service method was called
        verify(documentService).getAllDocuments();
    }

    @Test
    public void testGetDocumentsByCaseId_Success() {
        // Arrange
        when(documentService.getDocumentsByCaseId(anyLong())).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Document>>> response = documentController.getDocumentsByCaseId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocumentList, response.getBody().getData());

        // Verify service method was called
        verify(documentService).getDocumentsByCaseId(1L);
    }

    @Test
    public void testGetDocumentsByType() {
        // Arrange
        when(documentService.getDocumentsByType(any(DocumentType.class))).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Document>>> response = documentController.getDocumentsByType(DocumentType.EVIDENCE);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocumentList, response.getBody().getData());

        // Verify service method was called
        verify(documentService).getDocumentsByType(DocumentType.EVIDENCE);
    }

    @Test
    public void testSearchDocuments() {
        // Arrange
        when(documentService.searchDocumentsByTitle(anyString())).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Document>>> response = documentController.searchDocuments("Test");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocumentList, response.getBody().getData());

        // Verify service method was called
        verify(documentService).searchDocumentsByTitle("Test");
    }

    @Test
    public void testUpdateDocument_Success() {
        // Arrange
        when(documentService.updateDocument(anyLong(), any(Document.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Document>> response = documentController.updateDocument(1L, testDocument);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testDocument, response.getBody().getData());

        // Verify service method was called
        verify(documentService).updateDocument(1L, testDocument);
    }

    @Test
    public void testUpdateDocument_IdMismatch() {
        // Arrange
        testDocument.setId(1L);
        Document documentWithDifferentId = new Document();
        documentWithDifferentId.setId(2L);

        // Act
        ResponseEntity<ApiResponse<Document>> response = documentController.updateDocument(1L, documentWithDifferentId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("ID in the URL does not match the ID in the request body", response.getBody().getErrorMessages().get(0));
    }

    @Test
    public void testDeleteDocument_Success() {
        // Arrange
        when(documentService.deleteDocument(anyLong())).thenReturn(ApiResponse.success(null));

        // Act
        ResponseEntity<ApiResponse<Void>> response = documentController.deleteDocument(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        // Verify service method was called
        verify(documentService).deleteDocument(1L);
    }

    @Test
    public void testDeleteDocument_NotFound() {
        // Arrange
        when(documentService.deleteDocument(anyLong())).thenReturn(
                ApiResponse.error("Document not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Void>> response = documentController.deleteDocument(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Document not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(documentService).deleteDocument(1L);
    }

    @Test
    public void testGetDocumentContent_Success() {
        // Arrange
        when(documentService.getDocumentById(anyLong())).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<String>> response = documentController.getDocumentContent(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("This is test content", response.getBody().getData());

        // Verify service method was called
        verify(documentService).getDocumentById(1L);
    }

    @Test
    public void testGetDocumentContent_DocumentNotFound() {
        // Arrange
        when(documentService.getDocumentById(anyLong())).thenReturn(
                ApiResponse.error("Document not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<String>> response = documentController.getDocumentContent(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Document not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(documentService).getDocumentById(1L);
    }

    @Test
    public void testGetDocumentContent_NoContent() {
        // Arrange
        Document documentWithNoContent = new Document();
        documentWithNoContent.setId(1L);
        documentWithNoContent.setTitle("Test Document");
        documentWithNoContent.setType(DocumentType.EVIDENCE);
        documentWithNoContent.setCse(testCase);
        documentWithNoContent.setContent(null);

        ApiResponse<Document> responseWithNoContent = ApiResponse.success(documentWithNoContent);

        when(documentService.getDocumentById(anyLong())).thenReturn(responseWithNoContent);

        // Act
        ResponseEntity<ApiResponse<String>> response = documentController.getDocumentContent(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Document has no content", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(documentService).getDocumentById(1L);
    }
}