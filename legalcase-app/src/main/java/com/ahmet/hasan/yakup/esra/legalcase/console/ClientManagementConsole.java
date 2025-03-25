package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Client;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IClientService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.List;

public class ClientManagementConsole {
    private final IClientService clientService;
    private final ConsoleUtils utils;

    public ClientManagementConsole(IClientService clientService, ConsoleUtils utils) {
        this.clientService = clientService;
        this.utils = utils;
    }

    public void showMenu(User currentUser) {
        boolean returnToMain = false;
        while (!returnToMain) {
            printClientManagementMenu();
            int choice = ConsoleUtils.getUserChoice(utils.getScanner(), 8);
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

    public void viewAllClients() {
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
            utils.getLogger().error("Error retrieving all clients: ", e);
        }

        utils.waitForEnter();
    }

    public void searchClientById() {
        System.out.println("\n--- Search Client by ID ---");
        System.out.print("Enter Client ID: ");
        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

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
            utils.getLogger().error("Error searching client by ID: ", e);
        }

        utils.waitForEnter();
    }

    public void searchClientByEmail() {
        System.out.println("\n--- Search Client by Email ---");
        System.out.print("Enter Client Email: ");
        String email = utils.getScanner().nextLine();

        try {
            ApiResponse<Client> response = clientService.getClientByEmail(email);
            if (response.isSuccess()) {
                displayClientDetails(response.getData());
            } else {
                System.out.println("Client not found: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error searching client by email: ", e);
        }

        utils.waitForEnter();
    }

    public void searchClientsByName() {
        System.out.println("\n--- Search Clients by Name ---");
        System.out.print("Enter search term (name or surname): ");
        String term = utils.getScanner().nextLine();

        try {
            ApiResponse<List<Client>> response = clientService.searchClients(term);
            if (response.isSuccess()) {
                displayClientsList(response.getData());
            } else {
                System.out.println("Failed to search clients: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error searching clients by name: ", e);
        }

        utils.waitForEnter();
    }

    public void createNewClient() {
        System.out.println("\n--- Create New Client ---");

        Client newClient = new Client();

        System.out.print("First Name: ");
        String name = utils.getScanner().nextLine();
        newClient.setName(name);

        System.out.print("Last Name: ");
        String surname = utils.getScanner().nextLine();
        newClient.setSurname(surname);

        System.out.print("Email: ");
        String email = utils.getScanner().nextLine();
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
            utils.getLogger().error("Error creating new client: ", e);
        }

        utils.waitForEnter();
    }

    public void updateClient() {
        System.out.println("\n--- Update Client Details ---");
        System.out.print("Enter Client ID to update: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

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
            String input = utils.getScanner().nextLine();
            if (!input.isEmpty()) {
                clientToUpdate.setName(input);
            }

            System.out.print("Last Name [" + clientToUpdate.getSurname() + "]: ");
            input = utils.getScanner().nextLine();
            if (!input.isEmpty()) {
                clientToUpdate.setSurname(input);
            }

            System.out.print("Email [" + clientToUpdate.getEmail() + "]: ");
            input = utils.getScanner().nextLine();
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
            utils.getLogger().error("Error updating client: ", e);
        }

        utils.waitForEnter();
    }

    public void deleteClient() {
        System.out.println("\n--- Delete Client ---");
        System.out.print("Enter Client ID to delete: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            // First verify the client exists and show its details
            ApiResponse<Client> getResponse = clientService.getClientById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Client not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following client:");
            displayClientDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this client? (Y/N): ");
            String confirmation = utils.getScanner().nextLine();

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
            utils.getLogger().error("Error deleting client: ", e);
        }

        utils.waitForEnter();
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
                    utils.truncateString(client.getName(), 15),
                    utils.truncateString(client.getSurname(), 15),
                    utils.truncateString(client.getEmail(), 30));
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
}