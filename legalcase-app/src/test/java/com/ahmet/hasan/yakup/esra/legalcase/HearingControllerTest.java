package com.ahmet.hasan.yakup.esra.legalcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import com.ahmet.hasan.yakup.esra.legalcase.api.HearingController;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IHearingService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

/**
 * Test for HearingController using Mockito
 * This isolates tests from Spring context loading issues
 */
@ExtendWith(MockitoExtension.class)
public class HearingControllerTest {

    @Mock
    private IHearingService hearingService;

    @InjectMocks
    private HearingController hearingController;

    private Hearing testHearing;
    private Case testCase;
    private List<Hearing> testHearingList;
    private ApiResponse<Hearing> successResponse;
    private ApiResponse<Hearing> errorResponse;
    private ApiResponse<List<Hearing>> listSuccessResponse;
    private LocalDateTime testDate;
    private LocalDateTime futureDate;

    @BeforeEach
    public void setup() {
        // Setup test date
        testDate = LocalDateTime.now();
        futureDate = testDate.plusDays(7);

        // Setup test case
        testCase = new Case();
        testCase.setId(1L);
        testCase.setTitle("Test Case");

        // Setup test hearing
        testHearing = new Hearing();
        testHearing.setId(1L);
        testHearing.setCse(testCase);
        testHearing.setHearingDate(testDate);
        testHearing.setJudge("Test Judge");
        testHearing.setStatus(HearingStatus.SCHEDULED);
        testHearing.setLocation("Test Court Room");
        testHearing.setNotes("Test Notes");

        // Setup test list
        testHearingList = new ArrayList<>();
        testHearingList.add(testHearing);

        // Create response objects
        successResponse = ApiResponse.success(testHearing);
        errorResponse = ApiResponse.error("Test error message", HttpStatus.BAD_REQUEST.value());
        listSuccessResponse = ApiResponse.success(testHearingList);
    }

    @Test
    public void testCreateHearing_Success() {
        // Arrange
        when(hearingService.createHearing(any(Hearing.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.createHearing(testHearing);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearing, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).createHearing(any(Hearing.class));
    }

    @Test
    public void testCreateHearing_Failure() {
        // Arrange
        when(hearingService.createHearing(any(Hearing.class))).thenReturn(errorResponse);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.createHearing(testHearing);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals(errorResponse.getErrorMessages().get(0), response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(hearingService).createHearing(any(Hearing.class));
    }

    @Test
    public void testScheduleHearing_Success() {
        // Arrange
        when(hearingService.scheduleHearing(anyLong(), any(LocalDateTime.class), anyString(), anyString(), anyString()))
                .thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.scheduleHearing(
                1L, testDate, "Test Judge", "Test Court Room", "Test Notes");

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearing, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).scheduleHearing(1L, testDate, "Test Judge", "Test Court Room", "Test Notes");
    }

    @Test
    public void testGetHearingById_Success() {
        // Arrange
        when(hearingService.getHearingById(anyLong())).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.getHearingById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearing, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).getHearingById(1L);
    }

    @Test
    public void testGetHearingById_NotFound() {
        // Arrange
        when(hearingService.getHearingById(anyLong())).thenReturn(
                ApiResponse.error("Hearing not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.getHearingById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Hearing not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(hearingService).getHearingById(1L);
    }

    @Test
    public void testGetAllHearings() {
        // Arrange
        when(hearingService.getAllHearings()).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Hearing>>> response = hearingController.getAllHearings();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearingList, response.getBody().getData());
        assertEquals(1, response.getBody().getData().size());

        // Verify service method was called
        verify(hearingService).getAllHearings();
    }

    @Test
    public void testGetHearingsByCaseId_Success() {
        // Arrange
        when(hearingService.getHearingsByCaseId(anyLong())).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Hearing>>> response = hearingController.getHearingsByCaseId(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearingList, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).getHearingsByCaseId(1L);
    }

    @Test
    public void testGetHearingsByStatus() {
        // Arrange
        when(hearingService.getHearingsByStatus(any(HearingStatus.class))).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Hearing>>> response = hearingController.getHearingsByStatus(HearingStatus.SCHEDULED);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearingList, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).getHearingsByStatus(HearingStatus.SCHEDULED);
    }

    @Test
    public void testGetHearingsByDateRange() {
        // Arrange
        LocalDateTime start = testDate.minusDays(1);
        LocalDateTime end = testDate.plusDays(1);

        when(hearingService.getHearingsByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Hearing>>> response = hearingController.getHearingsByDateRange(start, end);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearingList, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).getHearingsByDateRange(start, end);
    }

    @Test
    public void testGetUpcomingHearings() {
        // Arrange
        when(hearingService.getUpcomingHearings()).thenReturn(listSuccessResponse);

        // Act
        ResponseEntity<ApiResponse<List<Hearing>>> response = hearingController.getUpcomingHearings();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearingList, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).getUpcomingHearings();
    }

    @Test
    public void testUpdateHearing_Success() {
        // Arrange
        when(hearingService.updateHearing(anyLong(), any(Hearing.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.updateHearing(1L, testHearing);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearing, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).updateHearing(1L, testHearing);
    }

    @Test
    public void testUpdateHearing_IdMismatch() {
        // Arrange
        testHearing.setId(1L);
        Hearing hearingWithDifferentId = new Hearing();
        hearingWithDifferentId.setId(2L);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.updateHearing(1L, hearingWithDifferentId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("ID in the URL does not match the ID in the request body", response.getBody().getErrorMessages().get(0));
    }

    @Test
    public void testUpdateHearingStatus_Success() {
        // Arrange
        when(hearingService.updateHearingStatus(anyLong(), any(HearingStatus.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.updateHearingStatus(1L, HearingStatus.COMPLETED);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearing, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).updateHearingStatus(1L, HearingStatus.COMPLETED);
    }

    @Test
    public void testRescheduleHearing_Success() {
        // Arrange
        when(hearingService.rescheduleHearing(anyLong(), any(LocalDateTime.class))).thenReturn(successResponse);

        // Act
        ResponseEntity<ApiResponse<Hearing>> response = hearingController.rescheduleHearing(1L, futureDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(testHearing, response.getBody().getData());

        // Verify service method was called
        verify(hearingService).rescheduleHearing(1L, futureDate);
    }

    @Test
    public void testDeleteHearing_Success() {
        // Arrange
        when(hearingService.deleteHearing(anyLong())).thenReturn(ApiResponse.success(null));

        // Act
        ResponseEntity<ApiResponse<Void>> response = hearingController.deleteHearing(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        // Verify service method was called
        verify(hearingService).deleteHearing(1L);
    }

    @Test
    public void testDeleteHearing_NotFound() {
        // Arrange
        when(hearingService.deleteHearing(anyLong())).thenReturn(
                ApiResponse.error("Hearing not found", HttpStatus.NOT_FOUND.value()));

        // Act
        ResponseEntity<ApiResponse<Void>> response = hearingController.deleteHearing(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Hearing not found", response.getBody().getErrorMessages().get(0));

        // Verify service method was called
        verify(hearingService).deleteHearing(1L);
    }
}