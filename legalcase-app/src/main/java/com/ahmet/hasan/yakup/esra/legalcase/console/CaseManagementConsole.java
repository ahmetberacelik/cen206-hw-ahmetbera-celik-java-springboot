package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.ArrayList;
import java.util.List;

public class CaseManagementConsole {
    private final ICaseService caseService;
    private final IClientService clientService;
    private final ConsoleUtils utils;

    public CaseManagementConsole(ICaseService caseService, IClientService clientService, ConsoleUtils utils) {
        this.caseService = caseService;
        this.clientService = clientService;
        this.utils = utils;
    }

    public void showMenu(User currentUser) {
        boolean returnToMain = false;
        while (!returnToMain) {
            printCaseManagementMenu();
            int choice = ConsoleUtils.getUserChoice(utils.getScanner(), 8);
            switch (choice) {
                case 1 -> viewAllCases();
                case 2 -> searchCaseById();
                case 3 -> searchCaseByCaseNumber();
                case 4 -> filterCasesByStatus();
                case 5 -> createNewCase();
                case 6 -> updateCase();
                case 7 -> deleteCase();
                case 8 -> returnToMain = true;
                default -> System.out.println("Invalid selection!");
            }
        }
    }

    private void printCaseManagementMenu() {
        System.out.println("\n--- Case Management ---");
        System.out.println("1. View All Cases");
        System.out.println("2. Search Case by ID");
        System.out.println("3. Search Case by Case Number");
        System.out.println("4. Filter Cases by Status");
        System.out.println("5. Create New Case");
        System.out.println("6. Update Existing Case");
        System.out.println("7. Delete Case");
        System.out.println("8. Return to Main Menu");
        System.out.print("Your choice: ");
    }

    public void viewAllCases() {
        System.out.println("\n--- All Cases ---");
        try {
            ApiResponse<List<Case>> response = caseService.getAllCases();
            if (response.isSuccess()) {
                displayCasesList(response.getData());
            } else {
                System.out.println("Failed to retrieve cases: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error retrieving all cases: ", e);
        }

        utils.waitForEnter();
    }

    public void searchCaseById() {
        System.out.println("\n--- Search Case by ID ---");
        System.out.print("Enter Case ID: ");
        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            ApiResponse<Case> response = caseService.getCaseById(id);
            if (response.isSuccess()) {
                displayCaseDetails(response.getData());
            } else {
                System.out.println("Case not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error searching case by ID: ", e);
        }

        utils.waitForEnter();
    }

    public void searchCaseByCaseNumber() {
        System.out.println("\n--- Search Case by Case Number ---");
        System.out.print("Enter Case Number: ");
        String caseNumber = utils.getScanner().nextLine();

        try {
            ApiResponse<Case> response = caseService.getCaseByCaseNumber(caseNumber);
            if (response.isSuccess()) {
                displayCaseDetails(response.getData());
            } else {
                System.out.println("Case not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error searching case by case number: ", e);
        }

        utils.waitForEnter();
    }

    public void filterCasesByStatus() {
        System.out.println("\n--- Filter Cases by Status ---");
        System.out.println("Select Status:");
        System.out.println("1. NEW");
        System.out.println("2. ACTIVE");
        System.out.println("3. PENDING");
        System.out.println("4. CLOSED");
        System.out.println("5. ARCHIVED");
        System.out.print("Your choice: ");

        int choice = ConsoleUtils.getUserChoice(utils.getScanner(), 5);
        CaseStatus status;
        switch (choice) {
            case 1 -> status = CaseStatus.NEW;
            case 2 -> status = CaseStatus.ACTIVE;
            case 3 -> status = CaseStatus.PENDING;
            case 4 -> status = CaseStatus.CLOSED;
            case 5 -> status = CaseStatus.ARCHIVED;
            default -> {
                System.out.println("Invalid choice!");
                return;
            }
        }

        try {
            ApiResponse<List<Case>> response = caseService.getCasesByStatus(status);
            if (response.isSuccess()) {
                displayCasesList(response.getData());
            } else {
                System.out.println("Failed to retrieve cases: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error filtering cases by status: ", e);
        }

        utils.waitForEnter();
    }

    public void createNewCase() {
        System.out.println("\n--- Create New Case ---");

        Case newCase = new Case();

        System.out.print("Case Number: ");
        String caseNumber = utils.getScanner().nextLine();
        newCase.setCaseNumber(caseNumber);

        System.out.print("Title: ");
        String title = utils.getScanner().nextLine();
        newCase.setTitle(title);

        System.out.print("Description: ");
        String description = utils.getScanner().nextLine();
        newCase.setDescription(description);

        System.out.println("Select Case Type:");
        System.out.println("1. CIVIL");
        System.out.println("2. CRIMINAL");
        System.out.println("3. FAMILY");
        System.out.println("4. CORPORATE");
        System.out.println("5. OTHER");
        System.out.print("Your choice: ");

        int typeChoice = ConsoleUtils.getUserChoice(utils.getScanner(), 5);
        CaseType caseType;
        switch (typeChoice) {
            case 1 -> caseType = CaseType.CIVIL;
            case 2 -> caseType = CaseType.CRIMINAL;
            case 3 -> caseType = CaseType.FAMILY;
            case 4 -> caseType = CaseType.CORPORATE;
            case 5 -> caseType = CaseType.OTHER;
            default -> {
                System.out.println("Invalid choice! Defaulting to OTHER.");
                caseType = CaseType.OTHER;
            }
        }
        newCase.setType(caseType);

        // Status is initially set to NEW
        newCase.setStatus(CaseStatus.NEW);

        try {
            ApiResponse<Case> response = caseService.createCase(newCase);
            if (response.isSuccess()) {
                System.out.println("Case created successfully with ID: " + response.getData().getId());

                // Ask if they want to assign clients to this case
                System.out.print("Do you want to assign clients to this case? (Y/N): ");
                String answer = utils.getScanner().nextLine();
                if (answer.equalsIgnoreCase("Y")) {
                    assignClientsToCase(response.getData().getId());
                }
            } else {
                System.out.println("Failed to create case: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error creating new case: ", e);
        }

        utils.waitForEnter();
    }

    public void updateCase() {
        System.out.println("\n--- Update Existing Case ---");
        System.out.print("Enter Case ID to update: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            // Fetch the case first
            ApiResponse<Case> getResponse = caseService.getCaseById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Case not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            Case caseToUpdate = getResponse.getData();

            System.out.println("Current Case Details:");
            displayCaseDetails(caseToUpdate);

            // Get updated information
            System.out.println("\nEnter new details (press Enter to keep current value):");

            System.out.print("Case Number [" + caseToUpdate.getCaseNumber() + "]: ");
            String input = utils.getScanner().nextLine();
            if (!input.isEmpty()) {
                caseToUpdate.setCaseNumber(input);
            }

            System.out.print("Title [" + caseToUpdate.getTitle() + "]: ");
            input = utils.getScanner().nextLine();
            if (!input.isEmpty()) {
                caseToUpdate.setTitle(input);
            }

            System.out.print("Description [" + caseToUpdate.getDescription() + "]: ");
            input = utils.getScanner().nextLine();
            if (!input.isEmpty()) {
                caseToUpdate.setDescription(input);
            }

            System.out.println("Current Case Type: " + caseToUpdate.getType());
            System.out.println("Select new Case Type (or press Enter to keep current):");
            System.out.println("1. CIVIL");
            System.out.println("2. CRIMINAL");
            System.out.println("3. FAMILY");
            System.out.println("4. CORPORATE");
            System.out.println("5. OTHER");
            System.out.print("Your choice: ");

            input = utils.getScanner().nextLine();
            if (!input.isEmpty()) {
                try {
                    int typeChoice = Integer.parseInt(input);
                    CaseType caseType;
                    switch (typeChoice) {
                        case 1 -> caseType = CaseType.CIVIL;
                        case 2 -> caseType = CaseType.CRIMINAL;
                        case 3 -> caseType = CaseType.FAMILY;
                        case 4 -> caseType = CaseType.CORPORATE;
                        case 5 -> caseType = CaseType.OTHER;
                        default -> {
                            System.out.println("Invalid choice! Keeping current value.");
                            caseType = caseToUpdate.getType();
                        }
                    }
                    caseToUpdate.setType(caseType);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Keeping current value.");
                }
            }

            System.out.println("Current Case Status: " + caseToUpdate.getStatus());
            System.out.println("Select new Case Status (or press Enter to keep current):");
            System.out.println("1. NEW");
            System.out.println("2. ACTIVE");
            System.out.println("3. PENDING");
            System.out.println("4. CLOSED");
            System.out.println("5. ARCHIVED");
            System.out.print("Your choice: ");

            input = utils.getScanner().nextLine();
            if (!input.isEmpty()) {
                try {
                    int statusChoice = Integer.parseInt(input);
                    CaseStatus status;
                    switch (statusChoice) {
                        case 1 -> status = CaseStatus.NEW;
                        case 2 -> status = CaseStatus.ACTIVE;
                        case 3 -> status = CaseStatus.PENDING;
                        case 4 -> status = CaseStatus.CLOSED;
                        case 5 -> status = CaseStatus.ARCHIVED;
                        default -> {
                            System.out.println("Invalid choice! Keeping current value.");
                            status = caseToUpdate.getStatus();
                        }
                    }
                    caseToUpdate.setStatus(status);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Keeping current value.");
                }
            }

            // Update the case
            ApiResponse<Case> updateResponse = caseService.updateCase(caseToUpdate);
            if (updateResponse.isSuccess()) {
                System.out.println("Case updated successfully!");

                // Ask if they want to manage clients for this case
                System.out.print("Do you want to manage clients for this case? (Y/N): ");
                String answer = utils.getScanner().nextLine();
                if (answer.equalsIgnoreCase("Y")) {
                    assignClientsToCase(id);
                }
            } else {
                System.out.println("Failed to update case: " + (updateResponse.getErrorMessages() != null ? updateResponse.getErrorMessages().get(0) : "Unknown error"));
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error updating case: ", e);
        }

        utils.waitForEnter();
    }

    public void assignClientsToCase(Long caseId) {
        System.out.println("\n--- Assign Clients to Case ---");

        try {
            // Fetch the case first
            ApiResponse<Case> caseResponse = caseService.getCaseById(caseId);
            if (!caseResponse.isSuccess()) {
                System.out.println("Case not found: " + (caseResponse.getErrorMessages() != null ? caseResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            Case caseEntity = caseResponse.getData();

            // Fetch all clients
            ApiResponse<List<Client>> clientsResponse = clientService.getAllClients();
            if (!clientsResponse.isSuccess() || clientsResponse.getData().isEmpty()) {
                System.out.println("No clients available to assign.");
                return;
            }

            // Display available clients
            System.out.println("Available Clients:");
            List<Client> clients = clientsResponse.getData();
            for (int i = 0; i < clients.size(); i++) {
                Client client = clients.get(i);
                System.out.println((i + 1) + ". " + client.getName() + " " + client.getSurname() + " (" + client.getEmail() + ")");
            }

            System.out.println("Enter client numbers to assign (comma-separated, e.g., 1,3,5): ");
            String selection = utils.getScanner().nextLine();

            if (!selection.isEmpty()) {
                String[] selections = selection.split(",");
                List<Client> selectedClients = new ArrayList<>();

                for (String sel : selections) {
                    try {
                        int index = Integer.parseInt(sel.trim()) - 1;
                        if (index >= 0 && index < clients.size()) {
                            selectedClients.add(clients.get(index));
                        } else {
                            System.out.println("Invalid selection: " + sel + ". Skipping.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid selection format: " + sel + ". Skipping.");
                    }
                }

                if (!selectedClients.isEmpty()) {
                    // Add clients to the case
                    for (Client client : selectedClients) {
                        caseEntity.addClient(client);
                    }

                    // Update the case in the database
                    ApiResponse<Case> updateResponse = caseService.updateCase(caseEntity);
                    if (updateResponse.isSuccess()) {
                        System.out.println("Successfully assigned " + selectedClients.size() + " client(s) to case ID " + caseId);
                    } else {
                        System.out.println("Failed to assign clients: " + (updateResponse.getErrorMessages() != null ? updateResponse.getErrorMessages().get(0) : "Unknown error"));
                    }
                } else {
                    System.out.println("No valid clients selected for assignment.");
                }
            } else {
                System.out.println("No clients selected for assignment.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error assigning clients to case: ", e);
        }
    }

    public void deleteCase() {
        System.out.println("\n--- Delete Case ---");
        System.out.print("Enter Case ID to delete: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            // First verify the case exists and show its details
            ApiResponse<Case> getResponse = caseService.getCaseById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Case not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following case:");
            displayCaseDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this case? (Y/N): ");
            String confirmation = utils.getScanner().nextLine();

            if (confirmation.equalsIgnoreCase("Y")) {
                ApiResponse<Void> deleteResponse = caseService.deleteCase(id);
                if (deleteResponse.isSuccess()) {
                    System.out.println("Case deleted successfully!");
                } else {
                    System.out.println("Failed to delete case: " + (deleteResponse.getErrorMessages() != null ? deleteResponse.getErrorMessages().get(0) : "Unknown error"));
                }
            } else {
                System.out.println("Case deletion cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error deleting case: ", e);
        }

        utils.waitForEnter();
    }

    private void displayCasesList(List<Case> cases) {
        if (cases == null || cases.isEmpty()) {
            System.out.println("No cases found.");
            return;
        }

        System.out.println("------------------------------------------------------------------");
        System.out.printf("%-5s | %-12s | %-30s | %-10s | %-10s%n",
                "ID", "Case Number", "Title", "Type", "Status");
        System.out.println("------------------------------------------------------------------");

        for (Case caseItem : cases) {
            System.out.printf("%-5d | %-12s | %-30s | %-10s | %-10s%n",
                    caseItem.getId(),
                    caseItem.getCaseNumber(),
                    utils.truncateString(caseItem.getTitle(), 30),
                    caseItem.getType(),
                    caseItem.getStatus());
        }

        System.out.println("------------------------------------------------------------------");
        System.out.println("Total cases: " + cases.size());
    }

    private void displayCaseDetails(Case caseItem) {
        if (caseItem == null) {
            System.out.println("No case details available.");
            return;
        }

        System.out.println("------------------------------------------------------------------");
        System.out.println("Case ID: " + caseItem.getId());
        System.out.println("Case Number: " + caseItem.getCaseNumber());
        System.out.println("Title: " + caseItem.getTitle());
        System.out.println("Type: " + caseItem.getType());
        System.out.println("Status: " + caseItem.getStatus());
        System.out.println("Description: " + caseItem.getDescription());
        System.out.println("Created At: " + caseItem.getCreatedAt());
        System.out.println("Updated At: " + caseItem.getUpdatedAt());

        // Display associated clients if available
        List<Client> clients = caseItem.getClients();
        if (clients != null && !clients.isEmpty()) {
            System.out.println("\nAssociated Clients:");
            for (Client client : clients) {
                System.out.println("- " + client.getName() + " " + client.getSurname() + " (" + client.getEmail() + ")");
            }
        } else {
            System.out.println("\nNo clients associated with this case.");
        }

        // Display associated hearings if available
        List<Hearing> hearings = caseItem.getHearings();
        if (hearings != null && !hearings.isEmpty()) {
            System.out.println("\nAssociated Hearings:");
            for (Hearing hearing : hearings) {
                System.out.println("- ID: " + hearing.getId() +
                        ", Date: " + hearing.getHearingDate() +
                        ", Judge: " + hearing.getJudge() +
                        ", Status: " + hearing.getStatus());
            }
        } else {
            System.out.println("\nNo hearings scheduled for this case.");
        }

        // Display associated documents if available
        List<Document> documents = caseItem.getDocuments();
        if (documents != null && !documents.isEmpty()) {
            System.out.println("\nAssociated Documents:");
            for (Document document : documents) {
                System.out.println("- ID: " + document.getId() +
                        ", Title: " + document.getTitle() +
                        ", Type: " + document.getType());
            }
        } else {
            System.out.println("\nNo documents associated with this case.");
        }

        System.out.println("------------------------------------------------------------------");
    }
}