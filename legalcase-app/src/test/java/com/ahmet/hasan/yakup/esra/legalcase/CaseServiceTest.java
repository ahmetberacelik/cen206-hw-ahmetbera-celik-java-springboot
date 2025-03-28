package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.CaseService;
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

class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private Logger logger;

    private CaseService caseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caseService = new CaseService(caseRepository);
    }

    // Helper method to create a test case
    private Case createTestCase() {
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        testCase.setStatus(CaseStatus.NEW);
        return testCase;
    }

    // Helper method to create a list of test cases
    private List<Case> createTestCasesList() {
        List<Case> cases = new ArrayList<>();
        cases.add(new Case(1L, "C-001", "Test Case 1", CaseType.CIVIL));
        cases.add(new Case(2L, "C-002", "Test Case 2", CaseType.CRIMINAL));
        cases.add(new Case(3L, "C-003", "Test Case 3", CaseType.FAMILY));
        return cases;
    }

    @Test
    void createCase_ValidCase_ReturnsSuccess() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.findByCaseNumber(testCase.getCaseNumber())).thenReturn(Optional.empty());
        when(caseRepository.save(any(Case.class))).thenReturn(testCase);

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testCase, response.getData());
        verify(caseRepository).findByCaseNumber(testCase.getCaseNumber());
        verify(caseRepository).save(testCase);
    }

    @Test
    void createCase_EmptyCaseNumber_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setCaseNumber("");

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case number cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void createCase_NullCaseNumber_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setCaseNumber(null);

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case number cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void createCase_DuplicateCaseNumber_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.findByCaseNumber(testCase.getCaseNumber())).thenReturn(Optional.of(testCase));

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("is already in use"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void createCase_EmptyTitle_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setTitle("");
        when(caseRepository.findByCaseNumber(testCase.getCaseNumber())).thenReturn(Optional.empty());

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case title cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void createCase_NullTitle_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setTitle(null);
        when(caseRepository.findByCaseNumber(testCase.getCaseNumber())).thenReturn(Optional.empty());

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case title cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void createCase_NullStatus_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setStatus(null);
        when(caseRepository.findByCaseNumber(testCase.getCaseNumber())).thenReturn(Optional.empty());

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case status cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void createCase_EnsuresStatusIsSetToNew() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setStatus(CaseStatus.ACTIVE); // Set to something other than NEW
        when(caseRepository.findByCaseNumber(testCase.getCaseNumber())).thenReturn(Optional.empty());
        when(caseRepository.save(any(Case.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApiResponse<Case> response = caseService.createCase(testCase);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(CaseStatus.NEW, response.getData().getStatus());
        verify(caseRepository).save(testCase);
    }

    @Test
    void getCaseById_ValidId_ReturnsCase() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.findById(1L)).thenReturn(Optional.of(testCase));

        // Act
        ApiResponse<Case> response = caseService.getCaseById(1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testCase, response.getData());
        verify(caseRepository).findById(1L);
    }

    @Test
    void getCaseById_InvalidId_ReturnsError() {
        // Arrange
        when(caseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Case> response = caseService.getCaseById(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository).findById(999L);
    }

    @Test
    void getCaseById_NullId_ReturnsError() {
        // Act
        ApiResponse<Case> response = caseService.getCaseById(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid case ID"));
        verify(caseRepository, never()).findById(any());
    }

    @Test
    void getCaseById_ZeroId_ReturnsError() {
        // Act
        ApiResponse<Case> response = caseService.getCaseById(0L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid case ID"));
        verify(caseRepository, never()).findById(any());
    }

    @Test
    void getCaseById_NegativeId_ReturnsError() {
        // Act
        ApiResponse<Case> response = caseService.getCaseById(-1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Invalid case ID"));
        verify(caseRepository, never()).findById(any());
    }

    @Test
    void getCaseByCaseNumber_ValidNumber_ReturnsCase() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.findByCaseNumber("C-001")).thenReturn(Optional.of(testCase));

        // Act
        ApiResponse<Case> response = caseService.getCaseByCaseNumber("C-001");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testCase, response.getData());
        verify(caseRepository).findByCaseNumber("C-001");
    }

    @Test
    void getCaseByCaseNumber_NonExistentNumber_ReturnsError() {
        // Arrange
        when(caseRepository.findByCaseNumber("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act
        ApiResponse<Case> response = caseService.getCaseByCaseNumber("NON-EXISTENT");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository).findByCaseNumber("NON-EXISTENT");
    }

    @Test
    void getCaseByCaseNumber_EmptyNumber_ReturnsError() {
        // Act
        ApiResponse<Case> response = caseService.getCaseByCaseNumber("");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case number cannot be empty"));
        verify(caseRepository, never()).findByCaseNumber(anyString());
    }

    @Test
    void getCaseByCaseNumber_NullNumber_ReturnsError() {
        // Act
        ApiResponse<Case> response = caseService.getCaseByCaseNumber(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case number cannot be empty"));
        verify(caseRepository, never()).findByCaseNumber(anyString());
    }

    @Test
    void getAllCases_ReturnsAllCases() {
        // Arrange
        List<Case> testCases = createTestCasesList();
        when(caseRepository.findAll()).thenReturn(testCases);

        // Act
        ApiResponse<List<Case>> response = caseService.getAllCases();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testCases, response.getData());
        assertEquals(3, response.getData().size());
        verify(caseRepository).findAll();
    }

    @Test
    void getAllCases_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(caseRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Case>> response = caseService.getAllCases();

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(caseRepository).findAll();
    }

    @Test
    void getCasesByStatus_ValidStatus_ReturnsCasesWithStatus() {
        // Arrange
        List<Case> activeCases = List.of(
                new Case(1L, "C-001", "Active Case 1", CaseType.CIVIL),
                new Case(2L, "C-002", "Active Case 2", CaseType.CRIMINAL)
        );
        when(caseRepository.findByStatus(CaseStatus.ACTIVE)).thenReturn(activeCases);

        // Act
        ApiResponse<List<Case>> response = caseService.getCasesByStatus(CaseStatus.ACTIVE);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(activeCases, response.getData());
        assertEquals(2, response.getData().size());
        verify(caseRepository).findByStatus(CaseStatus.ACTIVE);
    }

    @Test
    void getCasesByStatus_NullStatus_ReturnsError() {
        // Act
        ApiResponse<List<Case>> response = caseService.getCasesByStatus(null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case status cannot be empty"));
        verify(caseRepository, never()).findByStatus(any());
    }

    @Test
    void getCasesByStatus_NoMatchingCases_ReturnsEmptyList() {
        // Arrange
        when(caseRepository.findByStatus(CaseStatus.ARCHIVED)).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Case>> response = caseService.getCasesByStatus(CaseStatus.ARCHIVED);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(caseRepository).findByStatus(CaseStatus.ARCHIVED);
    }

    @Test
    void updateCase_ValidCase_ReturnsUpdatedCase() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setTitle("Updated Title");
        when(caseRepository.existsById(1L)).thenReturn(true);
        when(caseRepository.findByCaseNumber("C-001")).thenReturn(Optional.of(testCase));
        when(caseRepository.save(any(Case.class))).thenReturn(testCase);

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Updated Title", response.getData().getTitle());
        verify(caseRepository).existsById(1L);
        verify(caseRepository).findByCaseNumber("C-001");
        verify(caseRepository).save(testCase);
    }

    @Test
    void updateCase_NullId_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setId(null);

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case ID cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_EmptyCaseNumber_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setCaseNumber("");

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case number cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_NullCaseNumber_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setCaseNumber(null);

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case number cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_EmptyTitle_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setTitle("");

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case title cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_NullTitle_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setTitle(null);

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case title cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_NullStatus_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        testCase.setStatus(null);

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case status cannot be empty"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_NonExistentCase_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.existsById(1L)).thenReturn(false);

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_CaseNumberAlreadyInUse_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        Case existingCase = new Case(2L, "C-001", "Existing Case", CaseType.CIVIL);
        when(caseRepository.existsById(1L)).thenReturn(true);
        when(caseRepository.findByCaseNumber("C-001")).thenReturn(Optional.of(existingCase));

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.CONFLICT.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("already in use by another case"));
        verify(caseRepository, never()).save(any(Case.class));
    }

    @Test
    void updateCase_ExceptionThrown_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.existsById(1L)).thenReturn(true);
        when(caseRepository.findByCaseNumber("C-001")).thenReturn(Optional.of(testCase));
        when(caseRepository.save(any(Case.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Case> response = caseService.updateCase(testCase);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("An unexpected error occurred"));
        verify(caseRepository).save(testCase);
    }

    @Test
    void deleteCase_ExistingCase_ReturnsSuccess() {
        // Arrange
        when(caseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(caseRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = caseService.deleteCase(1L);

        // Assert
        assertTrue(response.isSuccess());
        verify(caseRepository).existsById(1L);
        verify(caseRepository).deleteById(1L);
    }

    @Test
    void deleteCase_NonExistentCase_ReturnsError() {
        // Arrange
        when(caseRepository.existsById(999L)).thenReturn(false);

        // Act
        ApiResponse<Void> response = caseService.deleteCase(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository, never()).deleteById(any());
    }

    @Test
    void deleteCase_ExceptionThrown_ReturnsError() {
        // Arrange
        when(caseRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(caseRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = caseService.deleteCase(1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("An unexpected error occurred"));
        verify(caseRepository).deleteById(1L);
    }
}