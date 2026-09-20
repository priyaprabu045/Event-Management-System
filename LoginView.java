package view;

import enums.Role;
import exception.LoginException;
import model.Admin;
import model.User;
import service.AuthService;
import util.AppScanner;
import util.DBConnection;
import view.admin.AdminView;

import java.sql.SQLException;

/**
 * Terminal user interface for system authentication and account registration.
 */
public class LoginView {

    private final AuthService authService;

    public LoginView() {
        this.authService = new AuthService();
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n==================================================");
            System.out.println("        EVENT MANAGEMENT SYSTEM (EMS)");
            System.out.println("==================================================");
            System.out.println("1. User Login");
            System.out.println("2. User Registration");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.println("==================================================");

            int choice = AppScanner.readInt("Enter your choice (1-4): ");

            switch (choice) {
                case 1 -> handleUserLogin();
                case 2 -> handleUserRegistration();
                case 3 -> handleAdminLogin();
                case 4 -> {
                    System.out.println("Thank you for using Event Management System. Goodbye!");
                    DBConnection.getInstance().closeConnection();
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please select an option between 1 and 4.");
            }
        }
    }

    private void handleUserLogin() {
        System.out.println("\n--- USER LOGIN ---");
        String email = AppScanner.readString("Enter Email: ");
        String password = AppScanner.readString("Enter Password: ");

        try {
            User user = authService.login(email, password, Role.USER);
            System.out.println("\nWelcome back, " + user.getName() + "!");
            System.out.println("Account Type: " + user.getRoleDescription());

            UserView userView = new UserView(user);
            userView.displayMenu();

        } catch (LoginException e) {
            System.out.println("Login Failed: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private void handleUserRegistration() {
        System.out.println("\n--- NEW USER REGISTRATION ---");
        String name = AppScanner.readString("Enter Full Name: ");
        String email = AppScanner.readString("Enter Email Address: ");
        String password = AppScanner.readString("Enter Password (min 4 characters): ");
        String phone = AppScanner.readString("Enter 10-digit Phone Number: ");

        try {
            User registeredUser = authService.registerUser(name, email, password, phone);
            System.out.println("\nRegistration Successful! Account created for " + registeredUser.getName());
            System.out.println("You can now login with your credentials.");
        } catch (LoginException e) {
            System.out.println("Registration Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private void handleAdminLogin() {
        System.out.println("\n--- ADMIN LOGIN ---");
        String email = AppScanner.readString("Enter Admin Email: ");
        String password = AppScanner.readString("Enter Admin Password: ");

        try {
            User user = authService.login(email, password, Role.ADMIN);
            if (user instanceof Admin admin) {
                System.out.println("\nAdmin Authentication Verified: " + admin.getName());
                System.out.println("Privilege Level: " + admin.getRoleDescription());

                AdminView adminView = new AdminView(admin);
                adminView.displayMenu();
            }
        } catch (LoginException e) {
            System.out.println("Admin Login Failed: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}
