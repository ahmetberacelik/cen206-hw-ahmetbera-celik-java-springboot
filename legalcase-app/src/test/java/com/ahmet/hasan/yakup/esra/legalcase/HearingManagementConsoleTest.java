package com.ahmet.hasan.yakup.esra.legalcase;

// Update these imports to match your project structure

import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import com.ahmet.hasan.yakup.esra.legalcase.console.HearingManagementConsole;
import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IHearingService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HearingManagementConsoleTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private IHearingService hearingService;
    private ICaseService caseService;
    private ConsoleUtils utils;
    private Scanner testScanner;
    private Logger mockLogger;
    private HearingManagementConsole hearingManagementConsole;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));

        // Set up mocks
        hearingService = Mockito.mock(IHearingService.class);
        caseService = Mockito.mock(ICaseService.class);
        mockLogger = Mockito.mock(Logger.class);

        testScanner = new Scanner(System.in);
        utils = Mockito.mock(ConsoleUtils.class);
        when(utils.getScanner()).thenReturn(testScanner);
        when(utils.getLogger()).thenReturn(mockLogger);

        hearingManagementConsole = new HearingManagementConsole(hearingService, caseService, utils);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a new console instance with simulated user input
     */
    private HearingManagementConsole createConsoleWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(utils.getScanner()).thenReturn(scanner);
        return new HearingManagementConsole(hearingService, caseService, utils);
    }

    /**
     * Helper method to create test hearing data
     */
    private List<Hearing> createTestHearings() {
        List<Hearing> hearings = new ArrayList<>();

        Case case1 = new Case(1L, "C-001", "Test Case 1", CaseType.CIVIL);
        case1.setStatus(CaseStatus.ACTIVE);

        Hearing hearing1 = new Hearing(1L, case1, LocalDateTime.now().plusDays(5), "Judge Smith");
        hearing1.setLocation("Courtroom A");
        hearing1.setStatus(HearingStatus.SCHEDULED);

        Hearing hearing2 = new Hearing(2L, case1, LocalDateTime.now().plusDays(10), "Judge Brown");
        hearing2.setLocation("Courtroom B");
        hearing2.setStatus(HearingStatus.SCHEDULED);

        hearings.add(hearing1);
        hearings.add(hearing2);

        return hearings;
    }

    @Test
    public void testViewAllHearings() {
        // Prepare test data
        List<Hearing> testHearings = createTestHearings();

        // Mock service response
        when(hearingService.getAllHearings()).thenReturn(ApiResponse.success(testHearings));

        // Execute the method
        hearingManagementConsole.viewAllHearings();

        // Verify interactions
        verify(hearingService).getAllHearings();
        verify(utils).waitForEnter();

        // Check output contains expected text
        String output = outContent.toString();
        assertTrue(output.contains("All Hearings"));
    }

    @Test
    public void testViewAllHearingsEmpty() {
        // Mock empty response
        when(hearingService.getAllHearings()).thenReturn(ApiResponse.success(new ArrayList<>()));

        // Execute the method
        hearingManagementConsole.viewAllHearings();

        // Verify interactions
        verify(hearingService).getAllHearings();
        verify(utils).waitForEnter();

        // Check output contains expected text
        String output = outContent.toString();
        assertTrue(output.contains("No hearings found"));
    }

    @Test
    public void testViewAllHearingsError() {
        // Mock error response
        when(hearingService.getAllHearings()).thenReturn(ApiResponse.error("Database error", 500));

        // Execute the method
        hearingManagementConsole.viewAllHearings();

        // Verify interactions
        verify(hearingService).getAllHearings();
        verify(utils).waitForEnter();

        // Check output contains expected text
        String output = outContent.toString();
        assertTrue(output.contains("Failed to retrieve hearings"));
    }

    @Test
    public void testViewHearingById() {
        // Setup scanner with input
        hearingManagementConsole = createConsoleWithInput("1\n");

        // Mock service response
        Hearing testHearing = createTestHearings().get(0);
        when(hearingService.getHearingById(1L)).thenReturn(ApiResponse.success(testHearing));

        // Execute the method
        hearingManagementConsole.viewHearingById();

        // Verify interactions
        verify(hearingService).getHearingById(1L);
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Hearing ID: 1"));
    }

    @Test
    public void testViewHearingByIdNotFound() {
        // Setup scanner with input
        hearingManagementConsole = createConsoleWithInput("99\n");

        // Mock service response - not found
        when(hearingService.getHearingById(99L)).thenReturn(ApiResponse.error("Hearing not found", 404));

        // Execute the method
        hearingManagementConsole.viewHearingById();

        // Verify interactions
        verify(hearingService).getHearingById(99L);
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Hearing not found"));
    }

    @Test
    public void testViewUpcomingHearings() {
        // Mock service response
        List<Hearing> upcomingHearings = createTestHearings();
        when(hearingService.getUpcomingHearings()).thenReturn(ApiResponse.success(upcomingHearings));

        // Execute the method
        hearingManagementConsole.viewUpcomingHearings();

        // Verify interactions
        verify(hearingService).getUpcomingHearings();
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Upcoming Hearings"));
    }

    @Test
    public void testScheduleNewHearing() {
        // Prepare input
        String input = "1\n2025-04-15 10:00\nJudge Smith\nCourtroom A\nTest hearing\n";
        hearingManagementConsole = createConsoleWithInput(input);

        // Mock responses
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        when(caseService.getCaseById(1L)).thenReturn(ApiResponse.success(testCase));

        Hearing newHearing = new Hearing();
        newHearing.setId(5L);
        when(hearingService.scheduleHearing(anyLong(), any(), anyString(), anyString(), anyString()))
                .thenReturn(ApiResponse.success(newHearing));

        // Execute method
        hearingManagementConsole.scheduleNewHearing();

        // Verify interactions
        verify(caseService).getCaseById(1L);
        verify(hearingService).scheduleHearing(eq(1L), any(), eq("Judge Smith"), eq("Courtroom A"), eq("Test hearing"));
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Hearing scheduled successfully"));
    }

    @Test
    public void testScheduleNewHearingCaseNotFound() {
        // Prepare input
        hearingManagementConsole = createConsoleWithInput("999\n");

        // Mock responses - case not found
        when(caseService.getCaseById(999L)).thenReturn(ApiResponse.error("Case not found", 404));

        // Execute method
        hearingManagementConsole.scheduleNewHearing();

        // Verify interactions
        verify(caseService).getCaseById(999L);
        verifyNoInteractions(hearingService); // Should not call hearing service

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Case not found"));
    }

    @Test
    public void testDeleteHearing() {
        // Prepare input with confirmation
        hearingManagementConsole = createConsoleWithInput("1\nY\n");

        // Mock responses
        Hearing testHearing = createTestHearings().get(0);
        when(hearingService.getHearingById(1L)).thenReturn(ApiResponse.success(testHearing));
        when(hearingService.deleteHearing(1L)).thenReturn(ApiResponse.success(null));

        // Execute method
        hearingManagementConsole.deleteHearing();

        // Verify interactions
        verify(hearingService).getHearingById(1L);
        verify(hearingService).deleteHearing(1L);
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Hearing deleted successfully"));
    }

    @Test
    public void testDeleteHearingCancelled() {
        // Prepare input with cancellation
        hearingManagementConsole = createConsoleWithInput("1\nN\n");

        // Mock responses
        Hearing testHearing = createTestHearings().get(0);
        when(hearingService.getHearingById(1L)).thenReturn(ApiResponse.success(testHearing));

        // Execute method
        hearingManagementConsole.deleteHearing();

        // Verify interactions
        verify(hearingService).getHearingById(1L);
        verify(hearingService, never()).deleteHearing(anyLong()); // Should not delete
        verify(utils).waitForEnter();

        // Check output
        String output = outContent.toString();
        assertTrue(output.contains("Hearing deletion cancelled"));
    }

    @Test
    public void testMenuTestTrue() {
        // Prepare a comprehensive input sequence that will exercise all menu options
        String input =
                "1\n" +  // View All Hearings
                        "2\n1\n" +  // View Hearing by ID
                        "3\n1\n" +  // View Hearings for a Case
                        "4\n" +  // View Upcoming Hearings

                        // Schedule New Hearing (full interaction)
                        "5\n1\n" +  // Case ID
                        "2025-04-15 10:00\n" +  // Hearing Date
                        "Judge Smith\n" +  // Judge Name
                        "Courtroom A\n" +  // Location
                        "Test Hearing Notes\n" +  // Notes

                        // Reschedule Hearing
                        "6\n1\n" +  // Hearing ID
                        "2025-05-20 14:30\n" +  // New Date

                        // Update Hearing Status
                        "7\n1\n" +  // Hearing ID
                        "2\n" +  // COMPLETED Status

                        // Delete Hearing (with confirmation)
                        "8\n1\nY\n" +  // Hearing ID and Confirmation

                        "9\n";  // Exit

        hearingManagementConsole = createConsoleWithInput(input);

        // Setup mock responses for each interaction
        Case testCase = new Case(1L, "C-001", "Test Case", CaseType.CIVIL);
        List<Hearing> testHearings = createTestHearings();
        Hearing testHearing = testHearings.get(0);

        // Mock responses for getAllHearings
        when(hearingService.getAllHearings())
                .thenReturn(ApiResponse.success(testHearings));

        // Mock responses for getHearingById
        when(hearingService.getHearingById(1L))
                .thenReturn(ApiResponse.success(testHearing));

        // Mock responses for getHearingsByCaseId
        when(hearingService.getHearingsByCaseId(1L))
                .thenReturn(ApiResponse.success(testHearings));

        // Mock responses for getUpcomingHearings
        when(hearingService.getUpcomingHearings())
                .thenReturn(ApiResponse.success(testHearings));

        // Mock responses for case service in scheduleHearing
        when(caseService.getCaseById(1L))
                .thenReturn(ApiResponse.success(testCase));

        // Mock hearing scheduling
        Hearing newHearing = new Hearing();
        newHearing.setId(5L);
        when(hearingService.scheduleHearing(
                eq(1L),
                any(LocalDateTime.class),
                eq("Judge Smith"),
                eq("Courtroom A"),
                eq("Test Hearing Notes")
        )).thenReturn(ApiResponse.success(newHearing));

        // Mock hearing rescheduling
        when(hearingService.rescheduleHearing(
                eq(1L),
                any(LocalDateTime.class)
        )).thenReturn(ApiResponse.success(testHearing));

        // Mock hearing status update
        when(hearingService.updateHearingStatus(
                eq(1L),
                eq(HearingStatus.COMPLETED)
        )).thenReturn(ApiResponse.success(testHearing));

        // Mock hearing deletion
        when(hearingService.deleteHearing(1L))
                .thenReturn(ApiResponse.success(null));

        // Execute the method with a mock user
        hearingManagementConsole.showMenu(new User());

        // Verify interactions for all menu options
        verify(hearingService).getAllHearings();
        verify(hearingService).getHearingsByCaseId(1L);
        verify(hearingService).getUpcomingHearings();
        verify(caseService).getCaseById(1L);
        verify(hearingService).scheduleHearing(
                eq(1L),
                any(LocalDateTime.class),
                eq("Judge Smith"),
                eq("Courtroom A"),
                eq("Test Hearing Notes")
        );
        verify(hearingService).rescheduleHearing(
                eq(1L),
                any(LocalDateTime.class)
        );
        verify(hearingService).updateHearingStatus(
                eq(1L),
                eq(HearingStatus.COMPLETED)
        );
        verify(hearingService).deleteHearing(1L);
    }

    @Test
    public void MenuTestFalse() {
        // Prepare input with cancellation
        hearingManagementConsole = createConsoleWithInput("1\n\n2\n2\n\n3\n2\n\n4\n\n5\n2\n\n6\n2\n\n7\n2\n\n8\n2\n\n9\n\n");

        // Mock responses
        Hearing testHearing = createTestHearings().get(0);
        when(hearingService.getHearingById(1L)).thenReturn(ApiResponse.success(testHearing));

        // Execute method
        hearingManagementConsole.showMenu(new User());

        // Verify interactions
        verify(hearingService, never()).deleteHearing(anyLong());
    }
}