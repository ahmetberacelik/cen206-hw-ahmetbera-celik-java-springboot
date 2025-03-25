package com.ahmet.hasan.yakup.esra.legalcase.console;

import org.slf4j.Logger;

import java.util.Scanner;

public class ConsoleUtils {
    private final Scanner scanner;
    private final Logger logger;

    public ConsoleUtils(Scanner scanner, Logger logger) {
        this.scanner = scanner;
        this.logger = logger;
    }

    public Scanner getScanner() {
        return scanner;
    }

    public Logger getLogger() {
        return logger;
    }

    public static int getUserChoice(Scanner scanner, int maxChoice) {
        int choice = -1;
        try {
            choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > maxChoice) {
                System.out.println("Please enter a number between 1-" + maxChoice + "!");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
        return choice;
    }

    public void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public String truncateString(String str, int maxLength) {
        if (str == null) {
            return "N/A";
        }

        if (str.length() <= maxLength) {
            return str;
        }

        return str.substring(0, maxLength - 3) + "...";
    }
}