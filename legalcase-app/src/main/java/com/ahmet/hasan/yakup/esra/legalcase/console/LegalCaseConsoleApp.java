package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.Hearing;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.CaseType;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.HearingStatus;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IDocumentService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IHearingService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * LegalCase Console Application - Complete implementation
 * Provides console interface for user authentication, case management, client tracking,
 * hearing scheduling, and document storage
 */
@Component
public class LegalCaseConsoleApp implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LegalCaseConsoleApp.class);
    private final Scanner scanner = new Scanner(System.in);
    private final IUserAuthenticationService authService;
    private final IUserService userService;
    private final ICaseService caseService;
    private final IClientService clientService;
    private final IHearingService hearingService;
    private final IDocumentService documentService;
    private User currentUser = null;
    private String authToken = null;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public LegalCaseConsoleApp(
            @Qualifier("primaryAuthService") IUserAuthenticationService authService,
            IUserService userService,
            ICaseService caseService,
            IClientService clientService,
            IHearingService hearingService,
            IDocumentService documentService) {
        this.authService = authService;
        this.userService = userService;
        this.caseService = caseService;
        this.clientService = clientService;
        this.hearingService = hearingService;
        this.documentService = documentService;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting LegalCase Console Application...");
        System.out.println("****************************************");
        System.out.println("* LEGAL CASE MANAGEMENT SYSTEM CONSOLE *");
        System.out.println("****************************************");

        boolean exit = false;
        while (!exit) {
            if (currentUser == null) {
                printLoginMenu();
                int choice = getUserChoice(3);
                switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> exit = true;
                    default -> System.out.println("Invalid selection!");
                }
            } else {
                printMainMenu();
                int choice = getUserChoice(7);
                switch (choice) {
                    case 1 -> viewProfile();
                    case 2 -> caseManagementMenu();
                    case 3 -> clientManagementMenu();
                    case 4 -> hearingManagementMenu();
                    case 5 -> documentManagementMenu();
                    case 6 -> logout();
                    case 7 -> exit = true;
                    default -> System.out.println("Invalid selection!");
                }
            }
        }

        System.out.println("Closing LegalCase Console Application...");
        scanner.close();
    }

    // ###################### MENU METHODS ######################

    private void printLoginMenu() {
        System.out.println("\n--- Login Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Your choice: ");
    }

    private void printMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Welcome, " + currentUser.getName() + " " + currentUser.getSurname() + " (" + currentUser.getRole() + ")");
        System.out.println("1. View My Profile");
        System.out.println("2. Case Management");
        System.out.println("3. Client Management");
        System.out.println("4. Hearing Management");
        System.out.println("5. Document Management");
        System.out.println("6. Logout");
        System.out.println("7. Exit Application");
        System.out.print("Your choice: ");
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

    private void printClientManagementMenu() {
        System.out.println("\n--- Client Management ---");
        System.out.println("1. View All Clients");
        System.out.println("2. Search Client by ID");
        System.out.println("3. Search Client by Email");
        System.out.println("4. Search Clients by Name");
        System.out.println("5. Create New Client");
        System.out.println("6. Update Client Details");
        System.out.println("7. Delete Client");
        System.out.println("8. Return to Main Menu");
        System.out.print("Your choice: ");
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

    private void printDocumentManagementMenu() {
        System.out.println("\n--- Document Management ---");
        System.out.println("1. View All Documents");
        System.out.println("2. View Document by ID");
        System.out.println("3. View Documents for a Case");
        System.out.println("4. Search Documents by Title");
        System.out.println("5. Create New Document");
        System.out.println("6. Update Document Details");
        System.out.println("7. View Document Content");
        System.out.println("8. Delete Document");
        System.out.println("9. Return to Main Menu");
        System.out.print("Your choice: ");
    }

    private int getUserChoice(int maxChoice) {
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

    // ###################### AUTHENTICATION METHODS ######################

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username or email: ");
        String usernameOrEmail = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            ApiResponse<Map<String, Object>> response = authService.authenticateUser(usernameOrEmail, password);

            if (response.isSuccess()) {
                Map<String, Object> data = response.getData();
                currentUser = (User) data.get("user");
                authToken = (String) data.get("token");

                System.out.println("Login successful! Welcome, " + currentUser.getName() + " " + currentUser.getSurname());
                logger.info("User logged in: {}", currentUser.getUsername());
            } else {
                System.out.println("Login failed: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
                logger.warn("Login failed: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            logger.error("Error during login: ", e);
        }
    }

    private void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("First Name: ");
        String name = scanner.nextLine();
        System.out.print("Last Name: ");
        String surname = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.println("Select User Role:");
        System.out.println("1. ADMIN");
        System.out.println("2. LAWYER");
        System.out.println("3. ASSISTANT");
        System.out.println("4. JUDGE");
        System.out.println("5. CLIENT");
        System.out.print("Your choice: ");
        int roleChoice = getUserChoice(5);

        UserRole role;
        switch (roleChoice) {
            case 1 -> role = UserRole.ADMIN;
            case 2 -> role = UserRole.LAWYER;
            case 3 -> role = UserRole.ASSISTANT;
            case 4 -> role = UserRole.JUDGE;
            case 5 -> role = UserRole.CLIENT;
            default -> {
                System.out.println("Invalid role! Defaulting to CLIENT.");
                role = UserRole.CLIENT;
            }
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setEnabled(true);

        try {
            ApiResponse<User> response = authService.registerUser(newUser);

            if (response.isSuccess()) {
                System.out.println("Registration successful! You can now login.");
                logger.info("New user registered: {}", newUser.getUsername());
            } else {
                System.out.println("Registration failed: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
                logger.warn("Registration failed: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            logger.error("Error during registration: ", e);
        }
    }

    private void logout() {
        if (authToken != null) {
            try {
                ApiResponse<Void> response = authService.logoutUser(authToken);

                if (response.isSuccess()) {
                    System.out.println("Successfully logged out.");
                    logger.info("User logged out: {}", currentUser.getUsername());
                } else {
                    System.out.println("An issue occurred during logout: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
                    logger.warn("Logout failed: {}", response.getErrorMessages());
                }
            } catch (Exception e) {
                System.out.println("An error occurred during logout: " + e.getMessage());
                logger.error("Error during logout: ", e);
            }
        }

        currentUser = null;
        authToken = null;
    }

    private void viewProfile() {
        if (currentUser != null) {
            System.out.println("\n--- User Profile ---");
            System.out.println("User ID: " + currentUser.getId());
            System.out.println("Username: " + currentUser.getUsername());
            System.out.println("Email: " + currentUser.getEmail());
            System.out.println("First Name: " + currentUser.getName());
            System.out.println("Last Name: " + currentUser.getSurname());
            System.out.println("Role: " + currentUser.getRole());
            System.out.println("Account Active: " + (currentUser.isEnabled() ? "Yes" : "No"));
            System.out.println("Keycloak ID: " + (currentUser.getKeycloakId() != null ? currentUser.getKeycloakId() : "None"));

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    // ###################### CASE MANAGEMENT METHODS ######################

    private void caseManagementMenu() {
        boolean returnToMain = false;
        while (!returnToMain) {
            printCaseManagementMenu();
            int choice = getUserChoice(8);
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

    private void viewAllCases() {
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
            logger.error("Error retrieving all cases: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchCaseById() {
        System.out.println("\n--- Search Case by ID ---");
        System.out.print("Enter Case ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());

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
            logger.error("Error searching case by ID: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchCaseByCaseNumber() {
        System.out.println("\n--- Search Case by Case Number ---");
        System.out.print("Enter Case Number: ");
        String caseNumber = scanner.nextLine();

        try {
            ApiResponse<Case> response = caseService.getCaseByCaseNumber(caseNumber);
            if (response.isSuccess()) {
                displayCaseDetails(response.getData());
            } else {
                System.out.println("Case not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error searching case by case number: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void filterCasesByStatus() {
        System.out.println("\n--- Filter Cases by Status ---");
        System.out.println("Select Status:");
        System.out.println("1. NEW");
        System.out.println("2. ACTIVE");
        System.out.println("3. PENDING");
        System.out.println("4. CLOSED");
        System.out.println("5. ARCHIVED");
        System.out.print("Your choice: ");

        int choice = getUserChoice(5);
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
            logger.error("Error filtering cases by status: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void createNewCase() {
        System.out.println("\n--- Create New Case ---");

        Case newCase = new Case();

        System.out.print("Case Number: ");
        String caseNumber = scanner.nextLine();
        newCase.setCaseNumber(caseNumber);

        System.out.print("Title: ");
        String title = scanner.nextLine();
        newCase.setTitle(title);

        System.out.print("Description: ");
        String description = scanner.nextLine();
        newCase.setDescription(description);

        System.out.println("Select Case Type:");
        System.out.println("1. CIVIL");
        System.out.println("2. CRIMINAL");
        System.out.println("3. FAMILY");
        System.out.println("4. CORPORATE");
        System.out.println("5. OTHER");
        System.out.print("Your choice: ");

        int typeChoice = getUserChoice(5);
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
                String answer = scanner.nextLine();
                if (answer.equalsIgnoreCase("Y")) {
                    assignClientsToCase(response.getData().getId());
                }
            } else {
                System.out.println("Failed to create case: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error creating new case: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateCase() {
        System.out.println("\n--- Update Existing Case ---");
        System.out.print("Enter Case ID to update: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

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
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                caseToUpdate.setCaseNumber(input);
            }

            System.out.print("Title [" + caseToUpdate.getTitle() + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                caseToUpdate.setTitle(input);
            }

            System.out.print("Description [" + caseToUpdate.getDescription() + "]: ");
            input = scanner.nextLine();
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

            input = scanner.nextLine();
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

            input = scanner.nextLine();
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
                String answer = scanner.nextLine();
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
            logger.error("Error updating case: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void assignClientsToCase(Long caseId) {
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
            String selection = scanner.nextLine();

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
            logger.error("Error assigning clients to case: ", e);
        }
    }

    private void deleteCase() {
        System.out.println("\n--- Delete Case ---");
        System.out.print("Enter Case ID to delete: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // First verify the case exists and show its details
            ApiResponse<Case> getResponse = caseService.getCaseById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Case not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following case:");
            displayCaseDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this case? (Y/N): ");
            String confirmation = scanner.nextLine();

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
            logger.error("Error deleting case: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
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
                    truncateString(caseItem.getTitle(), 30),
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

    // ###################### CLIENT MANAGEMENT METHODS ######################

    private void clientManagementMenu() {
        boolean returnToMain = false;
        while (!returnToMain) {
            printClientManagementMenu();
            int choice = getUserChoice(8);
            switch (choice) {
                case 1 -> viewAllClients();
                case 2 -> searchClientById();
                case 3 -> searchClientByEmail();
                case 4 -> searchClientsByName();
                case 5 -> createNewClient();
                case 6 -> updateClient();
                case 7 -> deleteClient();
                case 8 -> returnToMain = true;
                default -> System.out.println("Invalid selection!");
            }
        }
    }

    private void viewAllClients() {
        System.out.println("\n--- All Clients ---");
        try {
            ApiResponse<List<Client>> response = clientService.getAllClients();
            if (response.isSuccess()) {
                displayClientsList(response.getData());
            } else {
                System.out.println("Failed to retrieve clients: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error retrieving all clients: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchClientById() {
        System.out.println("\n--- Search Client by ID ---");
        System.out.print("Enter Client ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());

            ApiResponse<Client> response = clientService.getClientById(id);
            if (response.isSuccess()) {
                displayClientDetails(response.getData());
            } else {
                System.out.println("Client not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error searching client by ID: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchClientByEmail() {
        System.out.println("\n--- Search Client by Email ---");
        System.out.print("Enter Client Email: ");
        String email = scanner.nextLine();

        try {
            ApiResponse<Client> response = clientService.getClientByEmail(email);
            if (response.isSuccess()) {
                displayClientDetails(response.getData());
            } else {
                System.out.println("Client not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error searching client by email: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchClientsByName() {
        System.out.println("\n--- Search Clients by Name ---");
        System.out.print("Enter search term (name or surname): ");
        String term = scanner.nextLine();

        try {
            ApiResponse<List<Client>> response = clientService.searchClients(term);
            if (response.isSuccess()) {
                displayClientsList(response.getData());
            } else {
                System.out.println("Failed to search clients: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error searching clients by name: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void createNewClient() {
        System.out.println("\n--- Create New Client ---");

        Client newClient = new Client();

        System.out.print("First Name: ");
        String name = scanner.nextLine();
        newClient.setName(name);

        System.out.print("Last Name: ");
        String surname = scanner.nextLine();
        newClient.setSurname(surname);

        System.out.print("Email: ");
        String email = scanner.nextLine();
        newClient.setEmail(email);

        try {
            ApiResponse<Client> response = clientService.createClient(newClient);
            if (response.isSuccess()) {
                System.out.println("Client created successfully with ID: " + response.getData().getId());
            } else {
                System.out.println("Failed to create client: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error creating new client: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateClient() {
        System.out.println("\n--- Update Client Details ---");
        System.out.print("Enter Client ID to update: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // Fetch the client first
            ApiResponse<Client> getResponse = clientService.getClientById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Client not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            Client clientToUpdate = getResponse.getData();

            System.out.println("Current Client Details:");
            displayClientDetails(clientToUpdate);

            // Get updated information
            System.out.println("\nEnter new details (press Enter to keep current value):");

            System.out.print("First Name [" + clientToUpdate.getName() + "]: ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                clientToUpdate.setName(input);
            }

            System.out.print("Last Name [" + clientToUpdate.getSurname() + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                clientToUpdate.setSurname(input);
            }

            System.out.print("Email [" + clientToUpdate.getEmail() + "]: ");
            input = scanner.nextLine();
            if (!input.isEmpty()) {
                clientToUpdate.setEmail(input);
            }

            // Update the client
            ApiResponse<Client> updateResponse = clientService.updateClient(clientToUpdate);
            if (updateResponse.isSuccess()) {
                System.out.println("Client updated successfully!");
            } else {
                System.out.println("Failed to update client: " + (updateResponse.getErrorMessages() != null ? updateResponse.getErrorMessages().get(0) : "Unknown error"));
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error updating client: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void deleteClient() {
        System.out.println("\n--- Delete Client ---");
        System.out.print("Enter Client ID to delete: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // First verify the client exists and show its details
            ApiResponse<Client> getResponse = clientService.getClientById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Client not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following client:");
            displayClientDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this client? (Y/N): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("Y")) {
                ApiResponse<Void> deleteResponse = clientService.deleteClient(id);
                if (deleteResponse.isSuccess()) {
                    System.out.println("Client deleted successfully!");
                } else {
                    System.out.println("Failed to delete client: " + (deleteResponse.getErrorMessages() != null ? deleteResponse.getErrorMessages().get(0) : "Unknown error"));
                }
            } else {
                System.out.println("Client deletion cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error deleting client: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void displayClientsList(List<Client> clients) {
        if (clients == null || clients.isEmpty()) {
            System.out.println("No clients found.");
            return;
        }

        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-15s | %-15s | %-30s%n",
                "ID", "First Name", "Last Name", "Email");
        System.out.println("------------------------------------------------------------");

        for (Client client : clients) {
            System.out.printf("%-5d | %-15s | %-15s | %-30s%n",
                    client.getId(),
                    truncateString(client.getName(), 15),
                    truncateString(client.getSurname(), 15),
                    truncateString(client.getEmail(), 30));
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Total clients: " + clients.size());
    }

    private void displayClientDetails(Client client) {
        if (client == null) {
            System.out.println("No client details available.");
            return;
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Client ID: " + client.getId());
        System.out.println("First Name: " + client.getName());
        System.out.println("Last Name: " + client.getSurname());
        System.out.println("Email: " + client.getEmail());
        System.out.println("Created At: " + client.getCreatedAt());
        System.out.println("Updated At: " + client.getUpdatedAt());

        // Display associated cases if available
        List<Case> cases = client.getCases();
        if (cases != null && !cases.isEmpty()) {
            System.out.println("\nAssociated Cases:");
            for (Case caseItem : cases) {
                System.out.println("- ID: " + caseItem.getId() +
                        ", Number: " + caseItem.getCaseNumber() +
                        ", Title: " + caseItem.getTitle() +
                        ", Status: " + caseItem.getStatus());
            }
        } else {
            System.out.println("\nNo cases associated with this client.");
        }

        System.out.println("------------------------------------------------------------");
    }

    // ###################### HEARING MANAGEMENT METHODS ######################

    private void hearingManagementMenu() {
        boolean returnToMain = false;
        while (!returnToMain) {
            printHearingManagementMenu();
            int choice = getUserChoice(9);
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

    private void viewAllHearings() {
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
            logger.error("Error retrieving all hearings: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewHearingById() {
        System.out.println("\n--- View Hearing by ID ---");
        System.out.print("Enter Hearing ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());

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
            logger.error("Error viewing hearing by ID: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewHearingsForCase() {
        System.out.println("\n--- View Hearings for a Case ---");
        System.out.print("Enter Case ID: ");
        try {
            Long caseId = Long.parseLong(scanner.nextLine());

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
            logger.error("Error viewing hearings for case: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewUpcomingHearings() {
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
            logger.error("Error retrieving upcoming hearings: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void scheduleNewHearing() {
        System.out.println("\n--- Schedule New Hearing ---");

        // First select a case
        System.out.print("Enter Case ID for the hearing: ");
        Long caseId;
        try {
            caseId = Long.parseLong(scanner.nextLine());

            // Verify the case exists
            ApiResponse<Case> caseResponse = caseService.getCaseById(caseId);
            if (!caseResponse.isSuccess()) {
                System.out.println("Case not found: " + (caseResponse.getErrorMessages() != null ? caseResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            // Get hearing date and time
            System.out.print("Enter Hearing Date and Time (format: yyyy-MM-dd HH:mm): ");
            String dateTimeStr = scanner.nextLine();
            LocalDateTime hearingDate;
            try {
                hearingDate = LocalDateTime.parse(dateTimeStr, dateTimeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date/time format. Please use the format yyyy-MM-dd HH:mm.");
                return;
            }

            // Get judge name
            System.out.print("Enter Judge Name: ");
            String judge = scanner.nextLine();

            // Get location (optional)
            System.out.print("Enter Location (optional): ");
            String location = scanner.nextLine();

            // Get notes (optional)
            System.out.print("Enter Notes (optional): ");
            String notes = scanner.nextLine();

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
            logger.error("Error scheduling new hearing: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void rescheduleHearing() {
        System.out.println("\n--- Reschedule Hearing ---");
        System.out.print("Enter Hearing ID to reschedule: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

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
            String dateTimeStr = scanner.nextLine();
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
            logger.error("Error rescheduling hearing: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateHearingStatus() {
        System.out.println("\n--- Update Hearing Status ---");
        System.out.print("Enter Hearing ID: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

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

            int statusChoice = getUserChoice(4);
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
            logger.error("Error updating hearing status: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void deleteHearing() {
        System.out.println("\n--- Delete Hearing ---");
        System.out.print("Enter Hearing ID to delete: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // Verify the hearing exists
            ApiResponse<Hearing> getResponse = hearingService.getHearingById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Hearing not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following hearing:");
            displayHearingDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this hearing? (Y/N): ");
            String confirmation = scanner.nextLine();

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
            logger.error("Error deleting hearing: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
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
                    truncateString(caseNumber, 15),
                    truncateString(hearing.getJudge(), 20),
                    truncateString(hearing.getLocation(), 15),
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

    // ###################### DOCUMENT MANAGEMENT METHODS ######################

    private void documentManagementMenu() {
        boolean returnToMain = false;
        while (!returnToMain) {
            printDocumentManagementMenu();
            int choice = getUserChoice(9);
            switch (choice) {
                case 1 -> viewAllDocuments();
                case 2 -> viewDocumentById();
                case 3 -> viewDocumentsForCase();
                case 4 -> searchDocumentsByTitle();
                case 5 -> createNewDocument();
                case 6 -> updateDocumentDetails();
                case 7 -> viewDocumentContent();
                case 8 -> deleteDocument();
                case 9 -> returnToMain = true;
                default -> System.out.println("Invalid selection!");
            }
        }
    }

    private void viewAllDocuments() {
        System.out.println("\n--- All Documents ---");
        try {
            ApiResponse<List<Document>> response = documentService.getAllDocuments();
            if (response.isSuccess()) {
                displayDocumentsList(response.getData());
            } else {
                System.out.println("Failed to retrieve documents: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error retrieving all documents: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewDocumentById() {
        System.out.println("\n--- View Document by ID ---");
        System.out.print("Enter Document ID: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());

            ApiResponse<Document> response = documentService.getDocumentById(id);
            if (response.isSuccess()) {
                displayDocumentDetails(response.getData());
            } else {
                System.out.println("Document not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error viewing document by ID: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewDocumentsForCase() {
        System.out.println("\n--- View Documents for a Case ---");
        System.out.print("Enter Case ID: ");
        try {
            Long caseId = Long.parseLong(scanner.nextLine());

            ApiResponse<List<Document>> response = documentService.getDocumentsByCaseId(caseId);
            if (response.isSuccess()) {
                displayDocumentsList(response.getData());
            } else {
                System.out.println("Failed to retrieve documents: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error viewing documents for case: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void searchDocumentsByTitle() {
        System.out.println("\n--- Search Documents by Title ---");
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();

        try {
            ApiResponse<List<Document>> response = documentService.searchDocumentsByTitle(keyword);
            if (response.isSuccess()) {
                displayDocumentsList(response.getData());
            } else {
                System.out.println("Failed to search documents: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error searching documents by title: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void createNewDocument() {
        System.out.println("\n--- Create New Document ---");

        // Case ID'yi sor
        System.out.print("Enter Case ID for the document: ");
        Long caseId;
        try {
            caseId = Long.parseLong(scanner.nextLine());

            // Dava var mı diye kontrol et
            ApiResponse<Case> caseResponse = caseService.getCaseById(caseId);
            if (!caseResponse.isSuccess()) {
                System.out.println("Case not found: " + (caseResponse.getErrorMessages() != null ?
                        caseResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            // Belge başlığını al
            System.out.print("Enter Document Title: ");
            String title = scanner.nextLine();

            // Belge türünü al
            System.out.println("Select Document Type:");
            System.out.println("1. CONTRACT");
            System.out.println("2. EVIDENCE");
            System.out.println("3. PETITION");
            System.out.println("4. COURT_ORDER");
            System.out.println("5. OTHER");
            System.out.print("Your choice: ");

            int typeChoice = getUserChoice(5);
            DocumentType documentType;
            switch (typeChoice) {
                case 1 -> documentType = DocumentType.CONTRACT;
                case 2 -> documentType = DocumentType.EVIDENCE;
                case 3 -> documentType = DocumentType.PETITION;
                case 4 -> documentType = DocumentType.COURT_ORDER;
                case 5 -> documentType = DocumentType.OTHER;
                default -> {
                    System.out.println("Invalid choice! Defaulting to OTHER.");
                    documentType = DocumentType.OTHER;
                }
            }

            // İçerik için sor (dosya yolu yerine)
            System.out.println("Enter document content (type 'END' on a new line to finish):");
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equals("END")) {
                contentBuilder.append(line).append("\n");
            }
            String content = contentBuilder.toString();

            // Belgeyi oluştur
            ApiResponse<Document> createResponse = documentService.createDocumentWithContent(
                    caseId, title, documentType, content);

            if (createResponse.isSuccess()) {
                System.out.println("Document created successfully with ID: " + createResponse.getData().getId());
            } else {
                System.out.println("Failed to create document: " + (createResponse.getErrorMessages() != null ?
                        createResponse.getErrorMessages().get(0) : "Unknown error"));
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error creating new document: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void updateDocumentDetails() {
        System.out.println("\n--- Update Document Details ---");
        System.out.print("Enter Document ID to update: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // Verify the document exists
            ApiResponse<Document> getResponse = documentService.getDocumentById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Document not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            Document documentToUpdate = getResponse.getData();

            System.out.println("Current Document Details:");
            displayDocumentDetails(documentToUpdate);

            // Get updated information
            System.out.println("\nEnter new details (press Enter to keep current value):");

            System.out.print("Title [" + documentToUpdate.getTitle() + "]: ");
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                documentToUpdate.setTitle(input);
            }

            System.out.println("Current Document Type: " + documentToUpdate.getType());
            System.out.println("Select new Document Type (or press Enter to keep current):");
            System.out.println("1. CONTRACT");
            System.out.println("2. EVIDENCE");
            System.out.println("3. PETITION");
            System.out.println("4. COURT_ORDER");
            System.out.println("5. OTHER");
            System.out.print("Your choice: ");

            input = scanner.nextLine();
            if (!input.isEmpty()) {
                try {
                    int typeChoice = Integer.parseInt(input);
                    DocumentType documentType;
                    switch (typeChoice) {
                        case 1 -> documentType = DocumentType.CONTRACT;
                        case 2 -> documentType = DocumentType.EVIDENCE;
                        case 3 -> documentType = DocumentType.PETITION;
                        case 4 -> documentType = DocumentType.COURT_ORDER;
                        case 5 -> documentType = DocumentType.OTHER;
                        default -> {
                            System.out.println("Invalid choice! Keeping current value.");
                            documentType = documentToUpdate.getType();
                        }
                    }
                    documentToUpdate.setType(documentType);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Keeping current value.");
                }
            }

            // İçeriği güncellemek istiyor mu diye sor
            System.out.print("Do you want to update the document content? (Y/N): ");
            String updateContent = scanner.nextLine();
            if (updateContent.equalsIgnoreCase("Y")) {
                System.out.println("Enter new document content (type 'END' on a new line to finish):");
                StringBuilder contentBuilder = new StringBuilder();
                String line;
                while (!(line = scanner.nextLine()).equals("END")) {
                    contentBuilder.append(line).append("\n");
                }
                documentToUpdate.setContent(contentBuilder.toString());
            }

            // Update the document
            ApiResponse<Document> updateResponse = documentService.updateDocument(id, documentToUpdate);
            if (updateResponse.isSuccess()) {
                System.out.println("Document updated successfully!");
            } else {
                System.out.println("Failed to update document: " + (updateResponse.getErrorMessages() != null ? updateResponse.getErrorMessages().get(0) : "Unknown error"));
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error updating document: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void viewDocumentContent() {
        System.out.println("\n--- View Document Content ---");
        System.out.print("Enter Document ID: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // Belgenin var olup olmadığını kontrol et
            ApiResponse<Document> getResponse = documentService.getDocumentById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Document not found: " + (getResponse.getErrorMessages() != null ?
                        getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            Document document = getResponse.getData();

            System.out.println("\n=== Document Content ===");
            System.out.println("Title: " + document.getTitle());
            System.out.println("Type: " + document.getType());
            System.out.println("Content:\n" + document.getContent());
            System.out.println("=== End of Document ===");

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error viewing document content: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void deleteDocument() {
        System.out.println("\n--- Delete Document ---");
        System.out.print("Enter Document ID to delete: ");

        try {
            Long id = Long.parseLong(scanner.nextLine());

            // Verify the document exists
            ApiResponse<Document> getResponse = documentService.getDocumentById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Document not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following document:");
            displayDocumentDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this document? (Y/N): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("Y")) {
                ApiResponse<Void> deleteResponse = documentService.deleteDocument(id);
                if (deleteResponse.isSuccess()) {
                    System.out.println("Document deleted successfully!");
                } else {
                    System.out.println("Failed to delete document: " + (deleteResponse.getErrorMessages() != null ? deleteResponse.getErrorMessages().get(0) : "Unknown error"));
                }
            } else {
                System.out.println("Document deletion cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            logger.error("Error deleting document: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void displayDocumentsList(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            System.out.println("No documents found.");
            return;
        }

        System.out.println("------------------------------------------------------------------------");
        System.out.printf("%-5s | %-30s | %-12s | %-15s | %-20s%n",
                "ID", "Title", "Type", "Case Number", "Content Preview");
        System.out.println("------------------------------------------------------------------------");

        for (Document document : documents) {
            String caseNumber = (document.getCse() != null) ? document.getCse().getCaseNumber() : "N/A";
            String contentPreview = "N/A";
            if (document.getContent() != null && !document.getContent().isEmpty()) {
                contentPreview = document.getContent().length() > 20 ?
                        document.getContent().substring(0, 17) + "..." :
                        document.getContent();
                // Yeni satırları kaldır
                contentPreview = contentPreview.replace("\n", " ");
            }

            System.out.printf("%-5d | %-30s | %-12s | %-15s | %-20s%n",
                    document.getId(),
                    truncateString(document.getTitle(), 30),
                    document.getType(),
                    truncateString(caseNumber, 15),
                    truncateString(contentPreview, 20));
        }

        System.out.println("------------------------------------------------------------------------");
        System.out.println("Total documents: " + documents.size());
    }

    private void displayDocumentDetails(Document document) {
        if (document == null) {
            System.out.println("No document details available.");
            return;
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Document ID: " + document.getId());
        System.out.println("Title: " + document.getTitle());
        System.out.println("Type: " + document.getType());
        System.out.println("Case: " + ((document.getCse() != null) ?
                "ID: " + document.getCse().getId() +
                        ", Number: " + document.getCse().getCaseNumber() +
                        ", Title: " + document.getCse().getTitle()
                : "N/A"));

        // İçerik önizlemesi göster
        if (document.getContent() != null && !document.getContent().isEmpty()) {
            String contentPreview = document.getContent().length() > 100 ?
                    document.getContent().substring(0, 97) + "..." :
                    document.getContent();
            // Yeni satırları koruyarak göster
            System.out.println("Content Preview: \n----------");
            System.out.println(contentPreview);
            System.out.println("----------");
        } else {
            System.out.println("Content: <empty>");
        }

        System.out.println("Created At: " + document.getCreatedAt());
        System.out.println("Updated At: " + document.getUpdatedAt());
        System.out.println("------------------------------------------------------------");
    }

    // ###################### UTILITY METHODS ######################

    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return "N/A";
        }

        if (str.length() <= maxLength) {
            return str;
        }

        return str.substring(0, maxLength - 3) + "...";
    }
}