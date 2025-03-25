package com.ahmet.hasan.yakup.esra.legalcase.console;

import com.ahmet.hasan.yakup.esra.legalcase.model.Case;
import com.ahmet.hasan.yakup.esra.legalcase.model.Document;
import com.ahmet.hasan.yakup.esra.legalcase.model.User;
import com.ahmet.hasan.yakup.esra.legalcase.model.enums.DocumentType;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.ICaseService;
import com.ahmet.hasan.yakup.esra.legalcase.service.virtual.IDocumentService;
import com.ahmet.hasan.yakup.esra.legalcase.utils.ApiResponse;

import java.util.List;

public class DocumentManagementConsole {
    private final IDocumentService documentService;
    private final ICaseService caseService;
    private final ConsoleUtils utils;

    public DocumentManagementConsole(IDocumentService documentService, ICaseService caseService, ConsoleUtils utils) {
        this.documentService = documentService;
        this.caseService = caseService;
        this.utils = utils;
    }

    public void showMenu(User currentUser) {
        boolean returnToMain = false;
        while (!returnToMain) {
            printDocumentManagementMenu();
            int choice = ConsoleUtils.getUserChoice(utils.getScanner(), 9);
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

    public void viewAllDocuments() {
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
            utils.getLogger().error("Error retrieving all documents: ", e);
        }

        utils.waitForEnter();
    }

    public void viewDocumentById() {
        System.out.println("\n--- View Document by ID ---");
        System.out.print("Enter Document ID: ");
        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

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
            utils.getLogger().error("Error viewing document by ID: ", e);
        }

        utils.waitForEnter();
    }

    public void viewDocumentsForCase() {
        System.out.println("\n--- View Documents for a Case ---");
        System.out.print("Enter Case ID: ");
        try {
            Long caseId = Long.parseLong(utils.getScanner().nextLine());

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
            utils.getLogger().error("Error viewing documents for case: ", e);
        }

        utils.waitForEnter();
    }

    public void searchDocumentsByTitle() {
        System.out.println("\n--- Search Documents by Title ---");
        System.out.print("Enter search keyword: ");
        String keyword = utils.getScanner().nextLine();

        try {
            ApiResponse<List<Document>> response = documentService.searchDocumentsByTitle(keyword);
            if (response.isSuccess()) {
                displayDocumentsList(response.getData());
            } else {
                System.out.println("Failed to search documents: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Unknown error"));
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            utils.getLogger().error("Error searching documents by title: ", e);
        }

        utils.waitForEnter();
    }

    public void createNewDocument() {
        System.out.println("\n--- Create New Document ---");

        // Case ID'yi sor
        System.out.print("Enter Case ID for the document: ");
        Long caseId;
        try {
            caseId = Long.parseLong(utils.getScanner().nextLine());

            // Dava var mı diye kontrol et
            ApiResponse<Case> caseResponse = caseService.getCaseById(caseId);
            if (!caseResponse.isSuccess()) {
                System.out.println("Case not found: " + (caseResponse.getErrorMessages() != null ?
                        caseResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            // Belge başlığını al
            System.out.print("Enter Document Title: ");
            String title = utils.getScanner().nextLine();

            // Belge türünü al
            System.out.println("Select Document Type:");
            System.out.println("1. CONTRACT");
            System.out.println("2. EVIDENCE");
            System.out.println("3. PETITION");
            System.out.println("4. COURT_ORDER");
            System.out.println("5. OTHER");
            System.out.print("Your choice: ");

            int typeChoice = ConsoleUtils.getUserChoice(utils.getScanner(), 5);
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
            while (!(line = utils.getScanner().nextLine()).equals("END")) {
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
            utils.getLogger().error("Error creating new document: ", e);
        }

        utils.waitForEnter();
    }

    public void updateDocumentDetails() {
        System.out.println("\n--- Update Document Details ---");
        System.out.print("Enter Document ID to update: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

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
            String input = utils.getScanner().nextLine();
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

            input = utils.getScanner().nextLine();
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
            String updateContent = utils.getScanner().nextLine();
            if (updateContent.equalsIgnoreCase("Y")) {
                System.out.println("Enter new document content (type 'END' on a new line to finish):");
                StringBuilder contentBuilder = new StringBuilder();
                String line;
                while (!(line = utils.getScanner().nextLine()).equals("END")) {
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
            utils.getLogger().error("Error updating document: ", e);
        }

        utils.waitForEnter();
    }

    public void viewDocumentContent() {
        System.out.println("\n--- View Document Content ---");
        System.out.print("Enter Document ID: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

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
            utils.getLogger().error("Error viewing document content: ", e);
        }

        utils.waitForEnter();
    }

    public void deleteDocument() {
        System.out.println("\n--- Delete Document ---");
        System.out.print("Enter Document ID to delete: ");

        try {
            Long id = Long.parseLong(utils.getScanner().nextLine());

            // Verify the document exists
            ApiResponse<Document> getResponse = documentService.getDocumentById(id);
            if (!getResponse.isSuccess()) {
                System.out.println("Document not found: " + (getResponse.getErrorMessages() != null ? getResponse.getErrorMessages().get(0) : "Unknown error"));
                return;
            }

            System.out.println("You are about to delete the following document:");
            displayDocumentDetails(getResponse.getData());

            System.out.print("Are you sure you want to delete this document? (Y/N): ");
            String confirmation = utils.getScanner().nextLine();

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
            utils.getLogger().error("Error deleting document: ", e);
        }

        utils.waitForEnter();
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
                    utils.truncateString(document.getTitle(), 30),
                    document.getType(),
                    utils.truncateString(caseNumber, 15),
                    utils.truncateString(contentPreview, 20));
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
}