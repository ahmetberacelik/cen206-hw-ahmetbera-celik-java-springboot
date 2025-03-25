package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IHearingService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class HearingManagementConsole {
    private final IHearingService hearingService;
    private final ICaseService caseService;
    private final ConsoleUtils utils;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HearingManagementConsole(IHearingService hearingService, ICaseService caseService, ConsoleUtils utils) {
        this.hearingService = hearingService;
        this.caseService = caseService;
        this.utils = utils;
    }

    public void showMenu(User currentUser) {
        boolean returnToMain = false;
        while (!returnToMain) {
            printHearingManagementMenu();
            int choice = ConsoleUtils.getUserChoice(utils.getScanner(), 9);
            switch (choice) {
                case 1 -> viewAllHearings();
                case 2 -> viewHearingById();
                case 3 -> viewHearingsForCase();
                case 4 -> viewUpcomingHearings();
                case 5 -> scheduleNewHearing();
                case 6 -> rescheduleHearing();
                case 7 -> updateHearingStatus();
                case 8 -> deleteHearing();
                case 9 -> returnToMain = true;
                default -> System.out.println("Invalid selection!");
            }
        }
    }

    private void printHearingManagementMenu() {
        System.out.println("\n--- Hearing Management ---");
        System.out.println("1. View All Hearings");
        System.out.println("2. View Hearing by ID");
        System.out.println("3. View Hearings for a Case");
        System.out.println("4. View Upcoming Hearings");
        System.out.println("5. Schedule New Hearing");
        System.out.println("6. Reschedule Hearing");
        System.out.println("7. Update Hearing Status");
        System.out.println("8. Delete Hearing");
        System.out.println("9. Return to Main Menu");
        System.out.print("Your choice: ");
    }

    public void viewAllHearings() {
        System.out.println("\n--- All Hearings ---");
        try {
            ApiResponse<List<Hearing>> response = hearingService.getAllHearings();
            if (response.isSuccess()) {
                displayHearingsList(response.getData());
            } else {
                System.out.println("Failed to retrieve hearings: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error retrieving all hearings: ", e);
        }

        utils.waitForEnter();
    }

    public void viewHearingById() {
        System.out.println("\n--- View Hearing by ID ---");
        System.out.print("Enter Hearing ID: ");
        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            ApiResponse<Hearing> response = hearingService.getHearingById(id);
            if (response.isSuccess()) {
                displayHearingDetails(response.getData());
            } else {
                System.out.println("Hearing not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error viewing hearing by ID: ", e);
        }

        utils.waitForEnter();
    }

    public void viewHearingsForCase() {
        System.out.println("\n--- View Hearings for a Case ---");
        System.out.print("Enter Case ID: ");
        try {
            Long caseId = Long.parseLong(utils.getScanner().nextLine());

            ApiResponse<List<Hearing>> response = hearingService.getHearingsByCaseId(caseId);
            if (response.isSuccess()) {
                displayHearingsList(response.getData());
            } else {
                System.out.println("Failed to retrieve hearings: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error viewing hearings for case: ", e);
        }

        utils.waitForEnter();
    }

    public void viewUpcomingHearings() {
        System.out.println("\n--- Upcoming Hearings ---");
        try {
            ApiResponse<List<Hearing>> response = hearingService.getUpcomingHearings();
            if (response.isSuccess()) {
                displayHearingsList(response.getData());
            } else {
                System.out.println("Failed to retrieve upcoming hearings: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error retrieving upcoming hearings: ", e);
        }

        utils.waitForEnter();
    }

    public void scheduleNewHearing() {
        System.out.println("\n--- Schedule New Hearing ---");

        // First select a case
        System.out.print("Enter Case ID for the hearing: ");
        Long caseId;
        try {
            caseId = Long.parseLong(utils.getScanner().nextLine());

            // Verify the case exists
            ApiResponse<Case> caseResponse = caseService.getCaseById(caseId);
            if (!caseResponse.isSuccess()) {
                System.out.println("Case not found: " + (caseResponse.getErrorMessages() != null ? caseResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            // Get hearing date and time
            System.out.print("Enter Hearing Date and Time (format: yyyy-MM-dd HH:mm): ");
            String dateTimeStr = utils.getScanner().nextLine();
            LocalDateTime hearingDate;
            try {
                hearingDate = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date/time format. Please use the format yyyy-MM-dd HH:mm.");
                return;
            }

            // Get judge name
            System.out.print("Enter Judge Name: ");
            String judge = utils.getScanner().nextLine();

            // Get location (optional)
            System.out.print("Enter Location (optional): ");
            String location = utils.getScanner().nextLine();

            // Get notes (optional)
            System.out.print("Enter Notes (optional): ");
            String notes = utils.getScanner().nextLine();

            // Schedule the hearing
            ApiResponse<Hearing> scheduleResponse = hearingService.scheduleHearing(caseId, hearingDate, judge, location, notes);
            if (scheduleResponse.isSuccess()) {
                System.out.println("Hearing scheduled successfully with ID: " + scheduleResponse.getData().getId());
            } else {
                System.out.println("Failed to schedule hearing: " + (scheduleResponse.getErrorMessages() != null ? scheduleResponse.getErrorMessages().get(0) : "Unknown error"));
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error scheduling new hearing: ", e);
        }

        utils.waitForEnter();
    }

    public void rescheduleHearing() {
        System.out.println("\n--- Reschedule Hearing ---");
        System.out.print("Enter Hearing ID to reschedule: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            // Verify the hearing exists
            ApiResponse<Hearing> getResponse = hearingService.getHearingById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Hearing not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            Hearing hearing = getResponse.getData();
            System.out.println("Current Hearing Details:");
            displayHearingDetails(hearing);

            // Get new date/time
            System.out.print("Enter New Hearing Date and Time (format: yyyy-MM-dd HH:mm): ");
            String dateTimeStr = utils.getScanner().nextLine();
            LocalDateTime newDate;
            try {
                newDate = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date/time format. Please use the format yyyy-MM-dd HH:mm.");
                return;
            }

            // Reschedule the hearing
            ApiResponse<Hearing> rescheduleResponse = hearingService.rescheduleHearing(id, newDate);
            if (rescheduleResponse.isSuccess()) {
                System.out.println("Hearing rescheduled successfully!");
            } else {
                System.out.println("Failed to reschedule hearing: " + (rescheduleResponse.getErrorMessages() != null ? rescheduleResponse.getErrorMessages().get(0) : "Unknown error"));
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error rescheduling hearing: ", e);
        }

        utils.waitForEnter();
    }

    public void updateHearingStatus() {
        System.out.println("\n--- Update Hearing Status ---");
        System.out.print("Enter Hearing ID: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            // Verify the hearing exists
            ApiResponse<Hearing> getResponse = hearingService.getHearingById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Hearing not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            Hearing hearing = getResponse.getData();
            System.out.println("Current Hearing Status: " + hearing.getStatus());

            // Get new status
            System.out.println("Select new status:");
            System.out.println("1. SCHEDULED");
            System.out.println("2. COMPLETED");
            System.out.println("3. POSTPONED");
            System.out.println("4. CANCELLED");
            System.out.print("Your choice: ");

            int statusChoice = ConsoleUtils.getUserChoice(utils.getScanner(), 4);
            HearingStatus newStatus;
            switch (statusChoice) {
                case 1 -> newStatus = HearingStatus.SCHEDULED;
                case 2 -> newStatus = HearingStatus.COMPLETED;
                case 3 -> newStatus = HearingStatus.POSTPONED;
                case 4 -> newStatus = HearingStatus.CANCELLED;
                default -> {
                    System.out.println("Invalid choice! Status update cancelled.");
                    return;
                }
            }

            // Update the status
            ApiResponse<Hearing> updateResponse = hearingService.updateHearingStatus(id, newStatus);
            if (updateResponse.isSuccess()) {
                System.out.println("Hearing status updated successfully!");
            } else {
                System.out.println("Failed to update hearing status: " + (updateResponse.getErrorMessages() != null ? updateResponse.getErrorMessages().get(0) : "Unknown error"));
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error updating hearing status: ", e);
        }

        utils.waitForEnter();
    }

    public void deleteHearing() {
        System.out.println("\n--- Delete Hearing ---");
        System.out.print("Enter Hearing ID to delete: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            // Verify the hearing exists
            ApiResponse<Hearing> getResponse = hearingService.getHearingById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Hearing not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following hearing:");
            displayHearingDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this hearing? (Y/N): ");
            String confirmation = utils.getScanner().nextLine();

            if (confirmation.equalsIgnoreCase("Y")) {
                ApiResponse<Void> deleteResponse = hearingService.deleteHearing(id);
                if (deleteResponse.isSuccess()) {
                    System.out.println("Hearing deleted successfully!");
                } else {
                    System.out.println("Failed to delete hearing: " + (deleteResponse.getErrorMessages() != null ? deleteResponse.getErrorMessages().get(0) : "Unknown error"));
                }
            } else {
                System.out.println("Hearing deletion cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error deleting hearing: ", e);
        }

        utils.waitForEnter();
    }

    private void displayHearingsList(List<Hearing> hearings) {
        if (hearings == null || hearings.isEmpty()) {
            System.out.println("No hearings found.");
            return;
        }

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-20s | %-15s | %-10s%n",
                "ID", "Date & Time", "Case Number", "Judge", "Location", "Status");
        System.out.println("---------------------------------------------------------------------------------------------");

        for (Hearing hearing : hearings) {
            String caseNumber = (hearing.getCse() != null) ? hearing.getCse().getCaseNumber() : "N/A";
            System.out.printf("%-5d | %-20s | %-15s | %-20s | %-15s | %-10s%n",
                    hearing.getId(),
                    hearing.getHearingDate().format(dateTimeFormatter),
                    utils.truncateString(caseNumber, 15),
                    utils.truncateString(hearing.getJudge(), 20),
                    utils.truncateString(hearing.getLocation(), 15),
                    hearing.getStatus());
        }

        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("Total hearings: " + hearings.size());
    }

    private void displayHearingDetails(Hearing hearing) {
        if (hearing == null) {
            System.out.println("No hearing details available.");
            return;
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Hearing ID: " + hearing.getId());
        System.out.println("Date & Time: " + hearing.getHearingDate().format(dateTimeFormatter));
        System.out.println("Case: " + ((hearing.getCse() != null) ?
                "ID: " + hearing.getCse().getId() +
                        ", Number: " + hearing.getCse().getCaseNumber() +
                        ", Title: " + hearing.getCse().getTitle()
                : "N/A"));
        System.out.println("Judge: " + hearing.getJudge());
        System.out.println("Location: " + (hearing.getLocation() != null ? hearing.getLocation() : "N/A"));
        System.out.println("Status: " + hearing.getStatus());
        System.out.println("Notes: " + (hearing.getNotes() != null ? hearing.getNotes() : "N/A"));
        System.out.println("Created At: " + hearing.getCreatedAt());
        System.out.println("Updated At: " + hearing.getUpdatedAt());
        System.out.println("------------------------------------------------------------");
    }
}