package com.ahmet.hasan.yakup.esra.legalcase;

import com.ahmet.hasan.yakup.esra.legalcase.console.ConsoleUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsoleUtilsTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Logger mockLogger;
    private ConsoleUtils consoleUtils;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        mockLogger = Mockito.mock(Logger.class);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Helper method to create a ConsoleUtils instance with simulated input
     */
    private ConsoleUtils createConsoleUtilsWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new ConsoleUtils(scanner, mockLogger);
    }

    @Test
    public void testGetUserChoiceValid() {
        // Setup input with valid choice
        String input = "2\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = ConsoleUtils.getUserChoice(scanner, 3);

        assertEquals(2, result, "Should return the valid choice number");
    }

    @Test
    public void testGetUserChoiceTooHigh() {
        // Setup input with out of range choice (too high)
        String input = "5\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = ConsoleUtils.getUserChoice(scanner, 3);

        assertEquals(-1, result, "Should return -1 for choice above maximum");
        assertTrue(outContent.toString().contains("Please enter a number between 1-3"),
                "Should display range error message");
    }

    @Test
    public void testGetUserChoiceTooLow() {
        // Setup input with out of range choice (too low)
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = ConsoleUtils.getUserChoice(scanner, 3);

        assertEquals(-1, result, "Should return -1 for choice below minimum");
        assertTrue(outContent.toString().contains("Please enter a number between 1-3"),
                "Should display range error message");
    }

    @Test
    public void testGetUserChoiceInvalid() {
        // Setup input with invalid choice (not a number)
        String input = "abc\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        int result = ConsoleUtils.getUserChoice(scanner, 3);

        assertEquals(-1, result, "Should return -1 for non-numeric input");
        assertTrue(outContent.toString().contains("Please enter a valid number"),
                "Should display valid number error message");
    }

    @Test
    public void testWaitForEnter() {
        // Setup
        consoleUtils = createConsoleUtilsWithInput("\n");

        // Execute
        consoleUtils.waitForEnter();

        // Verify
        assertTrue(outContent.toString().contains("Press Enter to continue..."),
                "Should display waiting message");
    }

    @Test
    public void testTruncateStringNormal() {
        // Setup
        consoleUtils = createConsoleUtilsWithInput("");

        // Test normal length string (doesn't need truncation)
        String result = consoleUtils.truncateString("Hello", 10);

        assertEquals("Hello", result, "Should not truncate strings shorter than max length");
    }

    @Test
    public void testTruncateStringLong() {
        // Setup
        consoleUtils = createConsoleUtilsWithInput("");

        // Test long string that needs truncation
        String result = consoleUtils.truncateString("This is a very long string", 10);

        assertEquals(10, result.length(), "Truncated string should be exactly max length");
    }

    @Test
    public void testTruncateStringNull() {
        // Setup
        consoleUtils = createConsoleUtilsWithInput("");

        // Test null input
        String result = consoleUtils.truncateString(null, 10);

        assertEquals("N/A", result, "Should return N/A for null strings");
    }

    @Test
    public void testGetScanner() {
        // Setup
        Scanner mockScanner = new Scanner("test");
        consoleUtils = new ConsoleUtils(mockScanner, mockLogger);

        // Verify
        assertSame(mockScanner, consoleUtils.getScanner(), "Should return the same scanner instance");
    }

    @Test
    public void testGetLogger() {
        // Setup
        Scanner mockScanner = new Scanner("test");
        consoleUtils = new ConsoleUtils(mockScanner, mockLogger);

        // Verify
        assertSame(mockLogger, consoleUtils.getLogger(), "Should return the same logger instance");
    }
}