package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class LegalCaseConsoleApp implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LegalCaseConsoleApp.class);
    private final Scanner scanner = new Scanner(System.in);

    private final AuthenticationConsole authConsole;
    private final CaseManagementConsole caseConsole;
    private final ClientManagementConsole clientConsole;
    private final HearingManagementConsole hearingConsole;
    private final DocumentManagementConsole documentConsole;

    private User currentUser = null;
    private String authToken = null;

    @Autowired
    public LegalCaseConsoleApp(
            @Qualifier("primaryAuthService") IUserAuthenticationService authService,
            IUserService userService,
            ICaseService caseService,
            IClientService clientService,
            IHearingService hearingService,
            IDocumentService documentService) {

        ConsoleUtils utils = new ConsoleUtils(scanner, logger);

        this.authConsole = new AuthenticationConsole(authService, userService, utils);
        this.caseConsole = new CaseManagementConsole(caseService, clientService, utils);
        this.clientConsole = new ClientManagementConsole(clientService, utils);
        this.hearingConsole = new HearingManagementConsole(hearingService, caseService, utils);
        this.documentConsole = new DocumentManagementConsole(documentService, caseService, utils);
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
                int choice = ConsoleUtils.getUserChoice(scanner, 3);
                switch (choice) {
                    case 1 -> login();
                    case 2 -> register();
                    case 3 -> exit = true;
                    default -> System.out.println("Invalid selection!");
                }
            } else {
                printMainMenu();
                int choice = ConsoleUtils.getUserChoice(scanner, 7);
                switch (choice) {
                    case 1 -> viewProfile();
                    case 2 -> caseConsole.showMenu(currentUser);
                    case 3 -> clientConsole.showMenu(currentUser);
                    case 4 -> hearingConsole.showMenu(currentUser);
                    case 5 -> documentConsole.showMenu(currentUser);
                    case 6 -> logout();
                    case 7 -> exit = true;
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
        System.out.println("2. Case Management");
        System.out.println("3. Client Management");
        System.out.println("4. Hearing Management");
        System.out.println("5. Document Management");
        System.out.println("6. Logout");
        System.out.println("7. Exit Application");
        System.out.print("Your choice: ");
    }

    private void login() {
        var result = authConsole.login();
        if (result != null) {
            currentUser = result.getUser();
            authToken = result.getToken();
        }
    }

    private void register() {
        authConsole.register();
    }

    private void logout() {
        if (authToken != null) {
            authConsole.logout(authToken);
        }
        currentUser = null;
        authToken = null;
    }

    private void viewProfile() {
        if (currentUser != null) {
            authConsole.displayUserProfile(currentUser);
        }
    }
}