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
 * LegalCase Konsol Uygulaması - Keycloak kimlik doğrulama entegrasyonlu
 * Kullanıcı kaydı ve girişi için konsol arayüzü sağlar
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
        logger.info("LegalCase Konsol Uygulaması başlatılıyor...");
        System.out.println("****************************************");
        System.out.println("* LEGAL CASE YÖNETİM SİSTEMİ KONSOLU  *");
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
                    default -> System.out.println("Geçersiz seçim!");
                }
            } else {
                printMainMenu();
                int choice = getUserChoice(4);
                switch (choice) {
                    case 1 -> viewProfile();
                    case 2 -> listUsers();
                    case 3 -> logout();
                    case 4 -> exit = true;
                    default -> System.out.println("Geçersiz seçim!");
                }
            }
        }
        
        System.out.println("LegalCase Konsol Uygulaması kapatılıyor...");
        scanner.close();
    }

    private void printLoginMenu() {
        System.out.println("\n--- Giriş Menüsü ---");
        System.out.println("1. Giriş Yap");
        System.out.println("2. Kayıt Ol");
        System.out.println("3. Çıkış");
        System.out.print("Seçiminiz: ");
    }

    private void printMainMenu() {
        System.out.println("\n--- Ana Menü ---");
        System.out.println("Hoş geldiniz, " + currentUser.getName() + " " + currentUser.getSurname() + " (" + currentUser.getRole() + ")");
        System.out.println("1. Profilimi Görüntüle");
        System.out.println("2. Kullanıcıları Listele");
        System.out.println("3. Çıkış Yap");
        System.out.println("4. Uygulamadan Çık");
        System.out.print("Seçiminiz: ");
    }

    private int getUserChoice(int maxChoice) {
        int choice = -1;
        try {
            choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > maxChoice) {
                System.out.println("Lütfen 1-" + maxChoice + " arasında bir sayı girin!");
                return -1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Lütfen geçerli bir sayı girin!");
        }
        return choice;
    }

    private void login() {
        System.out.println("\n--- Giriş Yapın ---");
        System.out.print("Kullanıcı adı veya e-posta: ");
        String usernameOrEmail = scanner.nextLine();
        System.out.print("Şifre: ");
        String password = scanner.nextLine();

        // Keycloak ile giriş yapma
        try {
            ApiResponse<Map<String, Object>> response = authService.authenticateUser(usernameOrEmail, password);
            
            if (response.isSuccess()) {
                Map<String, Object> data = response.getData();
                currentUser = (User) data.get("user");
                authToken = (String) data.get("token");
                
                System.out.println("Giriş başarılı! Hoş geldiniz, " + currentUser.getName() + " " + currentUser.getSurname());
                logger.info("Kullanıcı giriş yaptı: {}", currentUser.getUsername());
            } else {
                System.out.println("Giriş başarısız: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Bilinmeyen hata"));
                logger.warn("Giriş başarısız: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("Giriş sırasında bir hata oluştu: " + e.getMessage());
            logger.error("Giriş sırasında hata: ", e);
        }
    }

    private void register() {
        System.out.println("\n--- Kayıt Olun ---");
        System.out.print("Kullanıcı adı: ");
        String username = scanner.nextLine();
        System.out.print("E-posta: ");
        String email = scanner.nextLine();
        System.out.print("Ad: ");
        String name = scanner.nextLine();
        System.out.print("Soyad: ");
        String surname = scanner.nextLine();
        System.out.print("Şifre: ");
        String password = scanner.nextLine();
        
        System.out.println("Kullanıcı Rolü Seçin:");
        System.out.println("1. ADMIN");
        System.out.println("2. LAWYER");
        System.out.println("3. ASSISTANT");
        System.out.println("4. JUDGE");
        System.out.println("5. CLIENT");
        System.out.print("Seçiminiz: ");
        int roleChoice = getUserChoice(5);
        
        UserRole role;
        switch (roleChoice) {
            case 1 -> role = UserRole.ADMIN;
            case 2 -> role = UserRole.LAWYER;
            case 3 -> role = UserRole.ASSISTANT;
            case 4 -> role = UserRole.JUDGE;
            case 5 -> role = UserRole.CLIENT;
            default -> {
                System.out.println("Geçersiz rol! Varsayılan olarak CLIENT atanıyor.");
                role = UserRole.CLIENT;
            }
        }
        
        // Yeni kullanıcı oluşturma
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setEnabled(true);
        
        // Keycloak ile kayıt
        try {
            ApiResponse<User> response = authService.registerUser(newUser);
            
            if (response.isSuccess()) {
                System.out.println("Kayıt başarılı! Şimdi giriş yapabilirsiniz.");
                logger.info("Yeni kullanıcı kaydoldu: {}", newUser.getUsername());
            } else {
                System.out.println("Kayıt başarısız: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Bilinmeyen hata"));
                logger.warn("Kayıt başarısız: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("Kayıt sırasında bir hata oluştu: " + e.getMessage());
            logger.error("Kayıt sırasında hata: ", e);
        }
    }

    private void logout() {
        if (authToken != null) {
            try {
                ApiResponse<Void> response = authService.logoutUser(authToken);
                
                if (response.isSuccess()) {
                    System.out.println("Başarıyla çıkış yapıldı.");
                    logger.info("Kullanıcı çıkış yaptı: {}", currentUser.getUsername());
                } else {
                    System.out.println("Çıkış sırasında bir sorun oluştu: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Bilinmeyen hata"));
                    logger.warn("Çıkış başarısız: {}", response.getErrorMessages());
                }
            } catch (Exception e) {
                System.out.println("Çıkış sırasında bir hata oluştu: " + e.getMessage());
                logger.error("Çıkış sırasında hata: ", e);
            }
        }
        
        currentUser = null;
        authToken = null;
    }

    private void viewProfile() {
        if (currentUser != null) {
            System.out.println("\n--- Kullanıcı Profili ---");
            System.out.println("Kullanıcı ID: " + currentUser.getId());
            System.out.println("Kullanıcı adı: " + currentUser.getUsername());
            System.out.println("E-posta: " + currentUser.getEmail());
            System.out.println("Ad: " + currentUser.getName());
            System.out.println("Soyad: " + currentUser.getSurname());
            System.out.println("Rol: " + currentUser.getRole());
            System.out.println("Hesap Etkin: " + (currentUser.isEnabled() ? "Evet" : "Hayır"));
            System.out.println("Keycloak ID: " + (currentUser.getKeycloakId() != null ? currentUser.getKeycloakId() : "Yok"));
            
            System.out.println("\nDevam etmek için Enter tuşuna basın...");
            scanner.nextLine();
        }
    }

    private void listUsers() {
        System.out.println("\n--- Kullanıcı Listesi ---");
        
        try {
            ApiResponse<List<User>> response = userService.getAllUsers();
            
            if (response.isSuccess()) {
                List<User> users = response.getData();
                if (users.isEmpty()) {
                    System.out.println("Hiç kullanıcı bulunamadı.");
                } else {
                    System.out.println("ID | Kullanıcı Adı | E-posta | Ad | Soyad | Rol");
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
                System.out.println("Kullanıcılar alınamadı: " + (response.getErrorMessages() != null ? response.getErrorMessages().get(0) : "Bilinmeyen hata"));
                logger.warn("Kullanıcı listesi alınamadı: {}", response.getErrorMessages());
            }
        } catch (Exception e) {
            System.out.println("Kullanıcılar listelenirken bir hata oluştu: " + e.getMessage());
            logger.error("Kullanıcı listeleme hatası: ", e);
        }
        
        System.out.println("\nDevam etmek için Enter tuşuna basın...");
        scanner.nextLine();
    }
} 