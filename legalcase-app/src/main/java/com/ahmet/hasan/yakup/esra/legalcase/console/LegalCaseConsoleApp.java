package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;
import java.util.List;

/**
 * LegalCase Console Application - With Keycloak authentication integration
 * Provides a console interface for user registration and login
 */
@Component
public class LegalCaseConsoleApp implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LegalCaseConsoleApp.class);
    private final Scanner scanner = new Scanner(System.in);
    private final IUserAuthenticationService authService;
    private final IUserService userService;
    private User currentUser = null;
    private String authToken = null;

    @Autowired
    public LegalCaseConsoleApp(
            @Qualifier("primaryAuthService") IUserAuthenticationService authService,
            IUserService userService) {
        this.authService = authService;
        this.userService = userService;
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
                int choice = getUserChoice(4);
                switch (choice) {
                    case 1 -> viewProfile();
                    case 2 -> listUsers();
                    case 3 -> logout();
                    case 4 -> exit = true;
                    default -> System.out.println("Invalid selection!");
                }
            }
        }

        System.out.println("Closing LegalCase Console Application...");
        scanner.close();
    }

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
        System.out.println("2. List Users");
        System.out.println("3. Logout");
        System.out.println("4. Exit Application");
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

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username or email: ");
        String usernameOrEmail = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Login with Keycloak
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

        // Register with Keycloak
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

    private void listUsers() {
        System.out.println("\n--- User List ---");

        try {
            ApiResponse<List<User>> response = userService.getAllUsers();

            if (response.isSuccess()) {
                List<User> users = response.getData();
                if (users.isEmpty()) {
                    System.out.println("No users found.");
                } else {
                    System.out.println("ID | Username | Email | First Name | Last Name | Role");
                    System.out.println("----------------------------------------------------");
                    for (User user : users) {
                        System.out.printf("%d | %s | %s | %s | %s | %s%n",
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getName(),
                                user.getSurname(),
                                user.getRole());
                    }
                }
            } else {
                System.out.println("Could not retrieve users: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
                logger.warn("Could not retrieve user list: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while listing users: " + e.getMessage());
            logger.error("Error listing users: ", e);
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}