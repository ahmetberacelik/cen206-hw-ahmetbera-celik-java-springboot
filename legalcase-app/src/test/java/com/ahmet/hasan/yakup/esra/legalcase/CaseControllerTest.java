package com.ahmet.hasan.yakup.esra.legalcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.ahmet.hasan.yakup.esra.legalcase.api.CaseController;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

/**
 * Test for CaseController using Mockito
 * This isolates tests from Spring context loading issues
 */
@ExtendWith(MockitoExtension.class)
public class CaseControllerTest {

    @Mock
    private ICaseService caseService;

    @InjectMocks
    private CaseController caseController;

    private Case testCase;
    private List<Case> testCaseList;
    private ApiResponse<Case> successResponse;
    private ApiResponse<Case> errorResponse;
    private ApiResponse<List<Case>> listSuccessResponse;

    @BeforeEach
    public void setup() {
        // Setup test data
        testCase = new Case();
        testCase.setId(1L);
        testCase.setTitle("Test Case");
        testCase.setDescription("Test Description");
        testCase.setStatus(CaseStatus.PENDING);

        testCaseList = new ArrayList<>();
        testCaseList.add(testCase);

        // Create response objects
        successResponse = ApiResponse.success(testCase);
        errorResponse = ApiResponse.error("Test error message", HttpStatus.BAD_REQUEST.value());
        listSuccessResponse = ApiResponse.success(testCaseList);
    }

    @Test
    public void testCreateCase_Success() {
        // Arrange
        when(caseService.createCase(any(Case.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Case>> response = caseController.createCase(testCase);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testCase, response.getBody().getData());

        // Verify service method was called
        verify(caseService).createCase(any(Case.class));
    }

    @Test
    public void testCreateCase_Failure() {
        // Arrange
        when(caseService.createCase(any(Case.class))).thenReturn(errorResponse);

        // Act
        ResponseEntity<ApiResponse<Case>> response = caseController.createCase(testCase);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorResponse.getErrorMessages().get(0), response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(caseService).createCase(any(Case.class));
    }

    @Test
    public void testGetCaseById_Success() {
        // Arrange
        when(caseService.getCaseById(anyLong())).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Case>> response = caseController.getCaseById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testCase, response.getBody().getData());

        // Verify service method was called
        verify(caseService).getCaseById(1L);
    }

    @Test
    public void testGetCaseById_NotFound() {
        // Arrange
        when(caseService.getCaseById(anyLong())).thenReturn(
                ApiResponse.error("Case not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Case>> response = caseController.getCaseById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Case not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(caseService).getCaseById(1L);
    }

    @Test
    public void testGetAllCases() {
        // Arrange
        when(caseService.getAllCases()).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Case>>> response = caseController.getAllCases();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testCaseList, response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());

        // Verify service method was called
        verify(caseService).getAllCases();
    }

    @Test
    public void testGetCasesByStatus() {
        // Arrange
        when(caseService.getCasesByStatus(any(CaseStatus.class))).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Case>>> response = caseController.getCasesByStatus(CaseStatus.PENDING);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testCaseList, response.getBody().getData());

        // Verify service method was called
        verify(caseService).getCasesByStatus(CaseStatus.PENDING);
    }

    @Test
    public void testUpdateCase_Success() {
        // Arrange
        when(caseService.updateCase(any(Case.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Case>> response = caseController.updateCase(1L, testCase);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testCase, response.getBody().getData());

        // Verify service method was called
        verify(caseService).updateCase(testCase);
    }

    @Test
    public void testUpdateCase_IdMismatch() {
        // Arrange
        testCase.setId(1L);

        // Act
        ResponseEntity<ApiResponse<Case>> response = caseController.updateCase(2L, testCase);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("ID in the URL does not match the ID in the request body", response.getBody().getErrorMessages().get(0));
    }

    @Test
    public void testUpdateCase_NotFound() {
        // Arrange
        when(caseService.updateCase(any(Case.class))).thenReturn(
                ApiResponse.error("Case not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Case>> response = caseController.updateCase(1L, testCase);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Case not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(caseService).updateCase(testCase);
    }

    @Test
    public void testDeleteCase_Success() {
        // Arrange
        when(caseService.deleteCase(anyLong())).thenReturn(ApiResponse.success(null));

        // Act
        ResponseEntity<ApiResponse<Void>> response = caseController.deleteCase(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        // Verify service method was called
        verify(caseService).deleteCase(1L);
    }

    @Test
    public void testDeleteCase_NotFound() {
        // Arrange
        when(caseService.deleteCase(anyLong())).thenReturn(
                ApiResponse.error("Case not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Void>> response = caseController.deleteCase(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Case not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(caseService).deleteCase(1L);
    }
}