package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.UserRole;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserAuthenticationService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IUserService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.List;
import java.util.Map;

public class AuthenticationConsole {
    private final IUserAuthenticationService authService;
    private final IUserService userService;
    private final ConsoleUtils utils;

    public AuthenticationConsole(IUserAuthenticationService authService, IUserService userService, ConsoleUtils utils) {
        this.authService = authService;
        this.userService = userService;
        this.utils = utils;
    }

    public static class LoginResult {
        private final User user;
        private final String token;

        public LoginResult(User user, String token) {
            this.user = user;
            this.token = token;
        }

        public User getUser() {
            return user;
        }

        public String getToken() {
            return token;
        }
    }

    public LoginResult login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username or email: ");
        String usernameOrEmail = utils.getScanner().nextLine();
        System.out.print("Password: ");
        String password = utils.getScanner().nextLine();

        try {
            ApiResponse<Map<String, Object>> response = authService.authenticateUser(usernameOrEmail, password);

            if (response.isSuccess()) {
                Map<String, Object> data = response.getData();
                User user = (User) data.get("user");
                String token = (String) data.get("token");

                System.out.println("Login successful! Welcome, " + user.getName() + " " + user.getSurname());
                utils.getLogger().info("User logged in: {}", user.getUsername());

                return new LoginResult(user, token);
            } else {
                System.out.println("Login failed: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
                utils.getLogger().warn("Login failed: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            utils.getLogger().error("Error during login: ", e);
        }

        utils.waitForEnter();
        return null;
    }

    public void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Username: ");
        String username = utils.getScanner().nextLine();
        System.out.print("Email: ");
        String email = utils.getScanner().nextLine();
        System.out.print("First Name: ");
        String name = utils.getScanner().nextLine();
        System.out.print("Last Name: ");
        String surname = utils.getScanner().nextLine();
        System.out.print("Password: ");
        String password = utils.getScanner().nextLine();

        System.out.println("Select User Role:");
        System.out.println("1. ADMIN");
        System.out.println("2. LAWYER");
        System.out.println("3. ASSISTANT");
        System.out.println("4. JUDGE");
        System.out.println("5. CLIENT");
        System.out.print("Your choice: ");
        int roleChoice = ConsoleUtils.getUserChoice(utils.getScanner(), 5);

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
                utils.getLogger().info("New user registered: {}", newUser.getUsername());
            } else {
                System.out.println("Registration failed: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
                utils.getLogger().warn("Registration failed: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            utils.getLogger().error("Error during registration: ", e);
        }

        utils.waitForEnter();
    }

    public void logout(String authToken) {
        try {
            ApiResponse<Void> response = authService.logoutUser(authToken);

            if (response.isSuccess()) {
                System.out.println("Successfully logged out.");
                utils.getLogger().info("User logged out");
            } else {
                System.out.println("An issue occurred during logout: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
                utils.getLogger().warn("Logout failed: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("An error occurred during logout: " + e.getMessage());
            utils.getLogger().error("Error during logout: ", e);
        }
    }

    public void displayUserProfile(User user) {
        System.out.println("\n--- User Profile ---");
        System.out.println("User ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("First Name: " + user.getName());
        System.out.println("Last Name: " + user.getSurname());
        System.out.println("Role: " + user.getRole());
        System.out.println("Account Active: " + (user.isEnabled() ? "Yes" : "No"));
        System.out.println("Keycloak ID: " + (user.getKeycloakId() != null ? user.getKeycloakId() : "None"));

        utils.waitForEnter();
    }

    public void listUsers() {
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
                utils.getLogger().warn("Could not retrieve user list: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while listing users: " + e.getMessage());
            utils.getLogger().error("Error listing users: ", e);
        }

        utils.waitForEnter();
    }
}