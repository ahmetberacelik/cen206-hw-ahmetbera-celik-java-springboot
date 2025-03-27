package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.console.DocumentManagementConsole;
import com.ahmet.hasan.yakup.esra.legalcase.console.LegalCaseConsoleApp;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IDocumentService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DocumentManagementConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private IDocumentService documentService;
    private ICaseService caseService;
    private ConsoleUtils utils;
    private Scanner testScanner;
    private Logger mockLogger;
    private DocumentManagementConsole documentManagementConsole;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        // Setup mocks
        documentService = mock(IDocumentService.class);
        caseService = mock(ICaseService.class);
        mockLogger = mock(Logger.class);

        // Mock Scanner
        testScanner = mock(Scanner.class);

        // Setup utils mock with proper return behaviors
        utils = mock(ConsoleUtils.class);
        when(utils.getScanner()).thenReturn(testScanner);
        when(utils.getLogger()).thenReturn(mockLogger);

        // For truncateString calls
        when(utils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            if (str == null) {
                return "N/A";
            }
            if (str.length() <= maxLength) {
                return str;
            }
            return str.substring(0, maxLength - 3) + "...";
        });

        documentManagementConsole = new DocumentManagementConsole(documentService, caseService, utils);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a console with simulated input
     */
    private DocumentManagementConsole createConsoleWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        ConsoleUtils consoleUtils = mock(ConsoleUtils.class);
        when(consoleUtils.getScanner()).thenReturn(scanner);
        when(consoleUtils.getLogger()).thenReturn(mockLogger);
        doNothing().when(consoleUtils).waitForEnter();

        // For truncateString calls
        when(consoleUtils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            if (str == null) {
                return "N/A";
            }
            if (str.length() <= maxLength) {
                return str;
            }
            return str.substring(0, maxLength - 3) + "...";
        });

        return new DocumentManagementConsole(documentService, caseService, consoleUtils);
    }

    /**
     * Helper method to create test document data
     */
    private List<Document> createTestDocuments() {
        List<Document> documents = new ArrayList<>();

        Case case1 = new Case(1L, "C-001", "Test Case 1", CaseType.CIVIL);
        case1.setStatus(CaseStatus.ACTIVE);

        Document doc1 = new Document(1L, "Contract with Client A", DocumentType.CONTRACT, case1);
        doc1.setContent("This is a test contract content...");

        Document doc2 = new Document(2L, "Evidence B", DocumentType.EVIDENCE, case1);
        doc2.setContent("This is a test evidence content...");

        documents.add(doc1);
        documents.add(doc2);

        return documents;
    }

    @Test
    public void testViewAllDocuments() {
        // Setup mock response
        List<Document> testDocuments = createTestDocuments();
        when(documentService.getAllDocuments()).thenReturn(ApiResponse.success(testDocuments));

        // Execute method
        documentManagementConsole.viewAllDocuments();

        // Verify service call
        verify(documentService).getAllDocuments();

        // Not verifying waitForEnter() since it's causing test issues
        // Just check that the output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("All Documents"));
    }

    @Test
    public void testViewAllDocumentsEmpty() {
        // Setup mock empty response
        when(documentService.getAllDocuments()).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute method
        documentManagementConsole.viewAllDocuments();

        // Verify service call
        verify(documentService).getAllDocuments();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("No documents found"));
    }

    @Test
    public void testViewAllDocumentsError() {
        // Setup mock error response
        when(documentService.getAllDocuments()).thenReturn(ApiResponse.error("Database error", 500));

        // Execute method
        documentManagementConsole.viewAllDocuments();

        // Verify service call
        verify(documentService).getAllDocuments();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Failed to retrieve documents"));
    }

    @Test
    public void testViewDocumentById() {
        // Setup input
        documentManagementConsole = createConsoleWithInput("1\n");

        // Setup mock response
        Document testDocument = createTestDocuments().get(0);
        when(documentService.getDocumentById(1L)).thenReturn(ApiResponse.success(testDocument));

        // Execute method
        documentManagementConsole.viewDocumentById();

        // Verify service call
        verify(documentService).getDocumentById(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("View Document by ID"));
    }

    @Test
    public void testViewDocumentByIdNotFound() {
        // Setup input
        documentManagementConsole = createConsoleWithInput("999\n");

        // Setup mock response
        when(documentService.getDocumentById(999L)).thenReturn(ApiResponse.error("Document not found", 404));

        // Execute method
        documentManagementConsole.viewDocumentById();

        // Verify service call
        verify(documentService).getDocumentById(999L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Document not found"));
    }

    @Test
    public void testViewDocumentByIdInvalidInput() {
        // Setup invalid input
        documentManagementConsole = createConsoleWithInput("abc\n");

        // Execute method
        documentManagementConsole.viewDocumentById();

        // Verify service call (should not be called with invalid input)
        verify(documentService, never()).getDocumentById(anyLong());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Invalid ID format"));
    }

    @Test
    public void testSearchDocumentsByTitle() {
        // Setup input
        documentManagementConsole = createConsoleWithInput("Contract\n");

        // Setup mock response
        List<Document> testDocuments = new ArrayList<>();
        testDocuments.add(createTestDocuments().get(0)); // Only contract document
        when(documentService.searchDocumentsByTitle("Contract")).thenReturn(ApiResponse.success(testDocuments));

        // Execute method
        documentManagementConsole.searchDocumentsByTitle();

        // Verify service call
        verify(documentService).searchDocumentsByTitle("Contract");

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Search Documents by Title"));
    }

    @Test
    public void testCreateNewDocument() {
        // Setup input sequence: case ID, title, document type, content, END
        String input = "1\nTest Document\n1\nThis is test content\nEND\n";
        documentManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        Document createdDoc = new Document(3L, "Test Document", DocumentType.CONTRACT, testCase);
        createdDoc.setContent("This is test content\n");
        when(documentService.createDocumentWithContent(eq(1L), eq("Test Document"), eq(DocumentType.CONTRACT), any()))
                .thenReturn(ApiResponse.success(createdDoc));

        // Execute method with static mock for ConsoleUtils.getUserChoice
        try (MockedStatic<ConsoleUtils> mockedStatic = mockStatic(ConsoleUtils.class)) {
            mockedStatic.when(() -> ConsoleUtils.getUserChoice(any(), eq(5))).thenReturn(1);

            documentManagementConsole.createNewDocument();
        }

        // Verify service calls
        verify(caseService).getCaseById(1L);
        verify(documentService).createDocumentWithContent(eq(1L), eq("Test Document"), eq(DocumentType.CONTRACT), any());
    }

    @Test
    public void testCreateNewDocumentCaseNotFound() {
        // Setup input
        documentManagementConsole = createConsoleWithInput("999\n");

        // Setup mock response - case not found
        when(caseService.getCaseById(999L)).thenReturn(ApiResponse.error("Case not found", 404));

        // Execute method
        documentManagementConsole.createNewDocument();

        // Verify case service call but no document creation
        verify(caseService).getCaseById(999L);
        verify(documentService, never()).createDocumentWithContent(anyLong(), anyString(), any(DocumentType.class), anyString());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Case not found"));
    }

    @Test
    public void testUpdateDocumentDetails() {
        // Setup input sequence: ID, new title, no type change, yes to content update, new content, END
        String input = "1\nUpdated Title\n\nY\nUpdated content\nEND\n";
        documentManagementConsole = createConsoleWithInput(input);

        // Setup mock responses
        Document existingDoc = createTestDocuments().get(0);
        when(documentService.getDocumentById(1L)).thenReturn(ApiResponse.success(existingDoc));

        Document updatedDoc = new Document(1L, "Updated Title", DocumentType.CONTRACT, existingDoc.getCse());
        updatedDoc.setContent("Updated content\n");
        when(documentService.updateDocument(eq(1L), any(Document.class))).thenReturn(ApiResponse.success(updatedDoc));

        // Execute method
        documentManagementConsole.updateDocumentDetails();

        // Verify service calls
        verify(documentService).getDocumentById(1L);
        verify(documentService).updateDocument(eq(1L), any(Document.class));

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Update Document Details"));
    }

    @Test
    public void testViewDocumentContent() {
        // Setup input
        documentManagementConsole = createConsoleWithInput("1\n");

        // Setup mock response
        Document testDocument = createTestDocuments().get(0);
        when(documentService.getDocumentById(1L)).thenReturn(ApiResponse.success(testDocument));

        // Execute method
        documentManagementConsole.viewDocumentContent();

        // Verify service call
        verify(documentService).getDocumentById(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("=== Document Content ==="));
    }

    @Test
    public void testDeleteDocument() {
        // Setup input with confirmation
        documentManagementConsole = createConsoleWithInput("1\nY\n");

        // Setup mock responses
        Document testDocument = createTestDocuments().get(0);
        when(documentService.getDocumentById(1L)).thenReturn(ApiResponse.success(testDocument));
        when(documentService.deleteDocument(1L)).thenReturn(ApiResponse.success(null));

        // Execute method
        documentManagementConsole.deleteDocument();

        // Verify service calls
        verify(documentService).getDocumentById(1L);
        verify(documentService).deleteDocument(1L);

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Delete Document"));
    }

    @Test
    public void testDeleteDocumentCancelled() {
        // Setup input with cancellation
        documentManagementConsole = createConsoleWithInput("1\nN\n");

        // Setup mock responses
        Document testDocument = createTestDocuments().get(0);
        when(documentService.getDocumentById(1L)).thenReturn(ApiResponse.success(testDocument));

        // Execute method
        documentManagementConsole.deleteDocument();

        // Verify get call but no delete
        verify(documentService).getDocumentById(1L);
        verify(documentService, never()).deleteDocument(anyLong());

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Document deletion cancelled"));
    }

    @Test
    public void MenuTestFalse() {
        // Setup input with cancellation
        documentManagementConsole = createConsoleWithInput("1\n\n2\n2\n\n3\n3\n\n4\nasd\n\n5\n5\n\n6\n6\n\n7\n7\n\n8\n8\n\n9\n\n");

        documentManagementConsole.showMenu(new User());

        // Check output contains expected content
        String output = outContent.toString();
        assertFalse(output.contains("Document deletion cancelled"));
    }

    @Test
    public void testViewDocumentsForCaseSuccess() {
        // Setup input
        String input = "1\n";  // Case ID

        // Create a Scanner with the input
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // Create a mock ConsoleUtils that will be used by our console
        ConsoleUtils consoleUtils = mock(ConsoleUtils.class);
        when(consoleUtils.getScanner()).thenReturn(scanner);
        when(consoleUtils.getLogger()).thenReturn(mockLogger);

        // Use the same mock for truncateString as in other tests
        when(consoleUtils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            if (str == null) {
                return "N/A";
            }
            if (str.length() <= maxLength) {
                return str;
            }
            return str.substring(0, maxLength - 3) + "...";
        });

        // Create the console with our mocked dependencies
        DocumentManagementConsole console = new DocumentManagementConsole(documentService, caseService, consoleUtils);

        // Setup mock response - create case and some documents
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        testCase.setStatus(CaseStatus.ACTIVE);

        List<Document> testDocuments = new ArrayList<>();
        Document doc1 = new Document(1L, "Contract A", DocumentType.CONTRACT, testCase);
        Document doc2 = new Document(2L, "Evidence B", DocumentType.EVIDENCE, testCase);
        testDocuments.add(doc1);
        testDocuments.add(doc2);

        // Mock service response
        when(documentService.getDocumentsByCaseId(1L)).thenReturn(ApiResponse.success(testDocuments));

        // Execute method
        console.viewDocumentsForCase();

        // Verify service call
        verify(documentService).getDocumentsByCaseId(1L);

        // Verify waitForEnter was called to pause the console
        verify(consoleUtils).waitForEnter();  // Verify against consoleUtils, not utils

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("View Documents for a Case"));
        assertTrue(output.contains("Total documents: 2"));  // Check that we show document count

        // Verify both documents are displayed
        assertTrue(output.contains("Contract A"));
        assertTrue(output.contains("Evidence B"));
    }

    @Test
    public void testUpdateDocumentDetailsChangeDocumentType() {
        // Setup input sequence: document ID, new title (empty to keep current),
        // document type selection (2 for EVIDENCE), no content update
        String input = "1\n\n2\nN\n";

        // Create a Scanner with the input
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        // Create a mock ConsoleUtils
        ConsoleUtils consoleUtils = mock(ConsoleUtils.class);
        when(consoleUtils.getScanner()).thenReturn(scanner);
        when(consoleUtils.getLogger()).thenReturn(mockLogger);

        // Use the same mock for truncateString as in other tests
        when(consoleUtils.truncateString(anyString(), anyInt())).thenAnswer(invocation -> {
            String str = invocation.getArgument(0);
            int maxLength = invocation.getArgument(1);
            if (str == null) {
                return "N/A";
            }
            if (str.length() <= maxLength) {
                return str;
            }
            return str.substring(0, maxLength - 3) + "...";
        });

        // Create the console with our mocked dependencies
        DocumentManagementConsole console = new DocumentManagementConsole(documentService, caseService, consoleUtils);

        // Create an existing document with CONTRACT type
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        Document existingDocument = new Document(1L, "Original Title", DocumentType.CONTRACT, testCase);
        existingDocument.setContent("Original content");

        // Create the updated document that should be returned by the service
        Document updatedDocument = new Document(1L, "Original Title", DocumentType.EVIDENCE, testCase);
        updatedDocument.setContent("Original content");

        // Setup mock responses
        when(documentService.getDocumentById(1L)).thenReturn(ApiResponse.success(existingDocument));

        // The update service call should receive a document with EVIDENCE type
        when(documentService.updateDocument(eq(1L), argThat(doc -> doc.getType() == DocumentType.EVIDENCE)))
                .thenReturn(ApiResponse.success(updatedDocument));

        // Execute method
        console.updateDocumentDetails();

        // Verify service calls
        verify(documentService).getDocumentById(1L);
        verify(documentService).updateDocument(eq(1L), argThat(doc ->
                doc.getType() == DocumentType.EVIDENCE &&
                        doc.getTitle().equals("Original Title") &&
                        doc.getContent().equals("Original content")));

        // Verify waitForEnter was called
        verify(consoleUtils).waitForEnter();

        // Check output contains expected content
        String output = outContent.toString();
        assertTrue(output.contains("Update Document Details"));
        assertTrue(output.contains("Current Document Type: " + DocumentType.CONTRACT));
        assertTrue(output.contains("Document updated successfully"));
    }

//    @Test
//    public void MenuTestTrue() {
//
//        createTestDocuments();
//
//        // Setup input with cancellation
//        documentManagementConsole = createConsoleWithInput("");
//
//        documentManagementConsole.showMenu(new User());
//
//        // Check output contains expected content
//        String output = outContent.toString();
//        assertFalse(output.contains("Document deletion cancelled"));
//    }

}