package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.repository.CaseRepository;
import com.ahmet.hasan.yakup.esra.legalcase.repository.HearingRepository;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import com.ahmet.hasan.yakup.esra.legalcase.service.concrete.HearingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HearingServiceTest {

    @Mock
    private HearingRepository hearingRepository;

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private Logger logger;

    private HearingService hearingService;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hearingService = new HearingService(hearingRepository, caseRepository);
        now = LocalDateTime.now();
    }

    // Helper method to create a test case
    private Case createTestCase() {
        return new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
    }

    // Helper method to create a test hearing
    private Hearing createTestHearing() {
        Case testCase = createTestCase();
        Hearing hearing = new Hearing(1L, testCase, now.plusDays(7), "Judge Smith");
        hearing.setLocation("Courtroom A");
        hearing.setStatus(HearingStatus.SCHEDULED);
        hearing.setNotes("Test hearing notes");
        return hearing;
    }

    // Helper method to create a list of test hearings
    private List<Hearing> createTestHearingsList() {
        List<Hearing> hearings = new ArrayList<>();
        Case testCase = createTestCase();

        Hearing hearing1 = new Hearing(1L, testCase, now.plusDays(7), "Judge Smith");
        hearing1.setStatus(HearingStatus.SCHEDULED);
        hearing1.setLocation("Courtroom A");

        Hearing hearing2 = new Hearing(2L, testCase, now.plusDays(14), "Judge Brown");
        hearing2.setStatus(HearingStatus.SCHEDULED);
        hearing2.setLocation("Courtroom B");

        Hearing hearing3 = new Hearing(3L, testCase, now.plusDays(21), "Judge Davis");
        hearing3.setStatus(HearingStatus.POSTPONED);
        hearing3.setLocation("Courtroom C");

        hearings.add(hearing1);
        hearings.add(hearing2);
        hearings.add(hearing3);

        return hearings;
    }

    @Test
    void createHearing_ValidHearing_ReturnsSuccess() {
        // Arrange
        Hearing testHearing = createTestHearing();
        when(caseRepository.findById(1L)).thenReturn(Optional.of(createTestCase()));
        when(hearingRepository.save(any(Hearing.class))).thenReturn(testHearing);

        // Act
        ApiResponse<Hearing> response = hearingService.createHearing(testHearing);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testHearing, response.getData());
        verify(caseRepository).findById(1L);
        verify(hearingRepository).save(testHearing);
    }

    @Test
    void createHearing_CaseNotFound_ReturnsError() {
        // Arrange
        Hearing testHearing = createTestHearing();
        when(caseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Hearing> response = hearingService.createHearing(testHearing);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository).findById(1L);
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void createHearing_NullHearingDate_ReturnsError() {
        // Arrange
        Hearing testHearing = createTestHearing();
        testHearing.setHearingDate(null);
        when(caseRepository.findById(1L)).thenReturn(Optional.of(createTestCase()));

        // Act
        ApiResponse<Hearing> response = hearingService.createHearing(testHearing);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Hearing date is required"));
        verify(caseRepository).findById(1L);
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void createHearing_RepositoryException_ReturnsError() {
        // Arrange
        Hearing testHearing = createTestHearing();
        when(caseRepository.findById(1L)).thenReturn(Optional.of(createTestCase()));
        when(hearingRepository.save(any(Hearing.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Hearing> response = hearingService.createHearing(testHearing);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to create hearing"));
        verify(caseRepository).findById(1L);
        verify(hearingRepository).save(testHearing);
    }

    @Test
    void scheduleHearing_ValidParameters_ReturnsSuccess() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.findById(1L)).thenReturn(Optional.of(testCase));

        Hearing savedHearing = new Hearing(1L, testCase, now.plusDays(7), "Judge Smith");
        savedHearing.setLocation("Courtroom A");
        savedHearing.setNotes("Test Notes");
        savedHearing.setStatus(HearingStatus.SCHEDULED);

        when(hearingRepository.save(any(Hearing.class))).thenReturn(savedHearing);

        // Act
        ApiResponse<Hearing> response = hearingService.scheduleHearing(
                1L, now.plusDays(7), "Judge Smith", "Courtroom A", "Test Notes");

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(savedHearing, response.getData());
        assertEquals(HearingStatus.SCHEDULED, response.getData().getStatus());
        verify(caseRepository).findById(1L);
        verify(hearingRepository).save(any(Hearing.class));
    }

    @Test
    void scheduleHearing_CaseNotFound_ReturnsError() {
        // Arrange
        when(caseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Hearing> response = hearingService.scheduleHearing(
                999L, now.plusDays(7), "Judge Smith", "Courtroom A", "Test Notes");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository).findById(999L);
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void scheduleHearing_RepositoryException_ReturnsError() {
        // Arrange
        Case testCase = createTestCase();
        when(caseRepository.findById(1L)).thenReturn(Optional.of(testCase));
        when(hearingRepository.save(any(Hearing.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Hearing> response = hearingService.scheduleHearing(
                1L, now.plusDays(7), "Judge Smith", "Courtroom A", "Test Notes");

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to schedule hearing"));
        verify(caseRepository).findById(1L);
        verify(hearingRepository).save(any(Hearing.class));
    }

    @Test
    void getHearingById_ValidId_ReturnsHearing() {
        // Arrange
        Hearing testHearing = createTestHearing();
        when(hearingRepository.findById(1L)).thenReturn(Optional.of(testHearing));

        // Act
        ApiResponse<Hearing> response = hearingService.getHearingById(1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testHearing, response.getData());
        verify(hearingRepository).findById(1L);
    }

    @Test
    void getHearingById_InvalidId_ReturnsError() {
        // Arrange
        when(hearingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Hearing> response = hearingService.getHearingById(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Hearing not found"));
        verify(hearingRepository).findById(999L);
    }

    @Test
    void getAllHearings_ReturnsAllHearings() {
        // Arrange
        List<Hearing> testHearings = createTestHearingsList();
        when(hearingRepository.findAll()).thenReturn(testHearings);

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getAllHearings();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testHearings, response.getData());
        assertEquals(3, response.getData().size());
        verify(hearingRepository).findAll();
    }

    @Test
    void getAllHearings_EmptyList_ReturnsEmptyList() {
        // Arrange
        when(hearingRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getAllHearings();

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().isEmpty());
        verify(hearingRepository).findAll();
    }

    @Test
    void getHearingsByCaseId_ValidCaseId_ReturnsHearings() {
        // Arrange
        List<Hearing> testHearings = createTestHearingsList();
        when(caseRepository.existsById(1L)).thenReturn(true);
        when(hearingRepository.findByCseId(1L)).thenReturn(testHearings);

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByCaseId(1L);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(testHearings, response.getData());
        assertEquals(3, response.getData().size());
        verify(caseRepository).existsById(1L);
        verify(hearingRepository).findByCseId(1L);
    }

    @Test
    void getHearingsByCaseId_CaseNotFound_ReturnsError() {
        // Arrange
        when(caseRepository.existsById(999L)).thenReturn(false);

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByCaseId(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(caseRepository).existsById(999L);
        verify(hearingRepository, never()).findByCseId(anyLong());
    }

    @Test
    void getHearingsByStatus_ValidStatus_ReturnsHearings() {
        // Arrange
        List<Hearing> scheduledHearings = List.of(
                createTestHearingsList().get(0),
                createTestHearingsList().get(1)
        );
        when(hearingRepository.findByStatus(HearingStatus.SCHEDULED)).thenReturn(scheduledHearings);

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByStatus(HearingStatus.SCHEDULED);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(scheduledHearings, response.getData());
        assertEquals(2, response.getData().size());
        verify(hearingRepository).findByStatus(HearingStatus.SCHEDULED);
    }

    @Test
    void getHearingsByDateRange_ValidRange_ReturnsHearings() {
        // Arrange
        List<Hearing> hearingsInRange = createTestHearingsList();
        LocalDateTime start = now;
        LocalDateTime end = now.plusDays(30);
        when(hearingRepository.findByHearingDateBetween(start, end)).thenReturn(hearingsInRange);

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByDateRange(start, end);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(hearingsInRange, response.getData());
        assertEquals(3, response.getData().size());
        verify(hearingRepository).findByHearingDateBetween(start, end);
    }

    @Test
    void getHearingsByDateRange_NullStartDate_ReturnsError() {
        // Arrange
        LocalDateTime end = now.plusDays(30);

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByDateRange(null, end);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Start and end dates are required"));
        verify(hearingRepository, never()).findByHearingDateBetween(any(), any());
    }

    @Test
    void getHearingsByDateRange_NullEndDate_ReturnsError() {
        // Arrange
        LocalDateTime start = now;

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByDateRange(start, null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Start and end dates are required"));
        verify(hearingRepository, never()).findByHearingDateBetween(any(), any());
    }

    @Test
    void getHearingsByDateRange_StartAfterEnd_ReturnsError() {
        // Arrange
        LocalDateTime start = now.plusDays(30);
        LocalDateTime end = now;

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getHearingsByDateRange(start, end);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Start date must be before end date"));
        verify(hearingRepository, never()).findByHearingDateBetween(any(), any());
    }

    @Test
    void getUpcomingHearings_ReturnsScheduledFutureHearings() {
        // Arrange
        List<Hearing> upcomingHearings = createTestHearingsList();
        when(hearingRepository.findByHearingDateAfterAndStatusNot(any(LocalDateTime.class), eq(HearingStatus.CANCELLED)))
                .thenReturn(upcomingHearings);

        // Act
        ApiResponse<List<Hearing>> response = hearingService.getUpcomingHearings();

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(upcomingHearings, response.getData());
        assertEquals(3, response.getData().size());
        verify(hearingRepository).findByHearingDateAfterAndStatusNot(any(LocalDateTime.class), eq(HearingStatus.CANCELLED));
    }

    @Test
    void updateHearing_HearingNotFound_ReturnsError() {
        // Arrange
        Hearing updatedHearing = createTestHearing();
        when(hearingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Hearing> response = hearingService.updateHearing(999L, updatedHearing);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Hearing not found"));
        verify(hearingRepository).findById(999L);
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void updateHearing_InvalidCase_ReturnsError() {
        // Arrange
        Hearing existingHearing = createTestHearing();
        Hearing updatedHearing = createTestHearing();
        Case invalidCase = new Case(999L, "C-999", "Invalid Case", CaseType.CIVIL);
        updatedHearing.setCse(invalidCase);

        when(hearingRepository.findById(1L)).thenReturn(Optional.of(existingHearing));
        when(caseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Hearing> response = hearingService.updateHearing(1L, updatedHearing);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Case not found"));
        verify(hearingRepository).findById(1L);
        verify(caseRepository).findById(999L);
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void updateHearingStatus_ValidStatus_ReturnsUpdatedHearing() {
        // Arrange
        Hearing existingHearing = createTestHearing();
        when(hearingRepository.findById(1L)).thenReturn(Optional.of(existingHearing));
        when(hearingRepository.save(any(Hearing.class))).thenAnswer(invocation -> {
            Hearing savedHearing = invocation.getArgument(0);
            return savedHearing;
        });

        // Act
        ApiResponse<Hearing> response = hearingService.updateHearingStatus(1L, HearingStatus.COMPLETED);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(HearingStatus.COMPLETED, response.getData().getStatus());
        verify(hearingRepository).findById(1L);
        verify(hearingRepository).save(any(Hearing.class));
    }

    @Test
    void updateHearingStatus_HearingNotFound_ReturnsError() {
        // Arrange
        when(hearingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Hearing> response = hearingService.updateHearingStatus(999L, HearingStatus.COMPLETED);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Hearing not found"));
        verify(hearingRepository).findById(999L);
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void rescheduleHearing_ValidNewDate_ReturnsRescheduledHearing() {
        // Arrange
        Hearing existingHearing = createTestHearing();
        LocalDateTime oldDate = existingHearing.getHearingDate();
        LocalDateTime newDate = now.plusDays(14);

        when(hearingRepository.findById(1L)).thenReturn(Optional.of(existingHearing));
        when(hearingRepository.save(any(Hearing.class))).thenAnswer(invocation -> {
            Hearing savedHearing = invocation.getArgument(0);
            return savedHearing;
        });

        // Act
        ApiResponse<Hearing> response = hearingService.rescheduleHearing(1L, newDate);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals(newDate, response.getData().getHearingDate());
        assertEquals(HearingStatus.SCHEDULED, response.getData().getStatus());
        assertTrue(response.getData().getNotes().contains("Hearing rescheduled from " + oldDate));
        verify(hearingRepository).findById(1L);
        verify(hearingRepository).save(any(Hearing.class));
    }

    @Test
    void rescheduleHearing_NullNewDate_ReturnsError() {
        // Act
        ApiResponse<Hearing> response = hearingService.rescheduleHearing(1L, null);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("New hearing date is required"));
        verify(hearingRepository, never()).findById(anyLong());
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void rescheduleHearing_HearingNotFound_ReturnsError() {
        // Arrange
        LocalDateTime newDate = now.plusDays(14);
        when(hearingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ApiResponse<Hearing> response = hearingService.rescheduleHearing(999L, newDate);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Hearing not found"));
        verify(hearingRepository).findById(999L);
        verify(hearingRepository, never()).save(any(Hearing.class));
    }

    @Test
    void rescheduleHearing_ExistingNotesAppended_ReturnsSuccess() {
        // Arrange
        Hearing existingHearing = createTestHearing();
        String existingNotes = existingHearing.getNotes();
        LocalDateTime oldDate = existingHearing.getHearingDate();
        LocalDateTime newDate = now.plusDays(14);

        when(hearingRepository.findById(1L)).thenReturn(Optional.of(existingHearing));
        when(hearingRepository.save(any(Hearing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApiResponse<Hearing> response = hearingService.rescheduleHearing(1L, newDate);

        // Assert
        assertTrue(response.isSuccess());
        assertTrue(response.getData().getNotes().contains(existingNotes));
        assertTrue(response.getData().getNotes().contains("Hearing rescheduled from " + oldDate));
        verify(hearingRepository).findById(1L);
        verify(hearingRepository).save(any(Hearing.class));
    }

    @Test
    void rescheduleHearing_RepositoryException_ReturnsError() {
        // Arrange
        Hearing existingHearing = createTestHearing();
        LocalDateTime newDate = now.plusDays(14);

        when(hearingRepository.findById(1L)).thenReturn(Optional.of(existingHearing));
        when(hearingRepository.save(any(Hearing.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ApiResponse<Hearing> response = hearingService.rescheduleHearing(1L, newDate);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to reschedule hearing"));
        verify(hearingRepository).findById(1L);
        verify(hearingRepository).save(any(Hearing.class));
    }

    @Test
    void deleteHearing_ExistingHearing_ReturnsSuccess() {
        // Arrange
        when(hearingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(hearingRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = hearingService.deleteHearing(1L);

        // Assert
        assertTrue(response.isSuccess());
        verify(hearingRepository).existsById(1L);
        verify(hearingRepository).deleteById(1L);
    }

    @Test
    void deleteHearing_HearingNotFound_ReturnsError() {
        // Arrange
        when(hearingRepository.existsById(999L)).thenReturn(false);

        // Act
        ApiResponse<Void> response = hearingService.deleteHearing(999L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Hearing not found"));
        verify(hearingRepository).existsById(999L);
        verify(hearingRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteHearing_RepositoryException_ReturnsError() {
        // Arrange
        when(hearingRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(hearingRepository).deleteById(1L);

        // Act
        ApiResponse<Void> response = hearingService.deleteHearing(1L);

        // Assert
        assertFalse(response.isSuccess());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getErrorCode());
        assertTrue(response.getErrorMessages().get(0).contains("Failed to delete hearing"));
        verify(hearingRepository).existsById(1L);
        verify(hearingRepository).deleteById(1L);
    }
}