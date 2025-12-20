package service;

import model.User;
import util.ConsoleUtils;
import java.util.Scanner;

public class AuthenticationService {
    private final UserManager userManager;
    private final Scanner scanner;

    public AuthenticationService(UserManager userManager, Scanner scanner) {
        this.userManager = userManager;
        this.scanner = scanner;
    }

    public User authenticate() {
        while (true) {
            ConsoleUtils.printWelcomeBanner();
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            
            if (!scanner.hasNextLine()) return null;
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("1")) {
                User user = loginLoop();
                if (user != null) return user;
            } else if (choice.equals("2")) {
                registerLoop();
            } else if (choice.equals("0")) {
                return null;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private User loginLoop() {
        while (true) {
            System.out.print("Enter username to login (or 'back'): ");
            if (!scanner.hasNextLine()) return null;
            String username = scanner.nextLine().trim();
            
            if (username.equalsIgnoreCase("back")) return null;
            
            if (userManager.userExists(username)) {
                User user = userManager.findUser(username);
                
                System.out.print("Enter password: ");
                if (!scanner.hasNextLine()) return null;
                String password = scanner.nextLine().trim();
                
                if (password.equals(user.getPassword())) {
                    ConsoleUtils.printWelcomeBanner();
                    System.out.println(ConsoleUtils.formatSuccess("Welcome, " + user.getName() + "!"));
                    return user;
                } else {
                    System.out.println(ConsoleUtils.formatError("Invalid password."));
                }
            } else {
                System.out.println("User not found. Please try again.");
            }
        }
    }

    private void registerLoop() {
        System.out.println("\n--- Create New Account ---");
        
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine().trim();
        while (firstName.isEmpty()) {
            System.out.print("First Name cannot be empty. Enter First Name: ");
            firstName = scanner.nextLine().trim();
        }
        
        System.out.print("Enter Last Name (optional): ");
        String lastName = scanner.nextLine().trim();
        
        System.out.print("Choose Username: ");
        String username = scanner.nextLine().trim();
        while (username.isEmpty() || username.contains(" ") || userManager.userExists(username)) {
            if (userManager.userExists(username)) {
                System.out.println(ConsoleUtils.formatError("Username already taken."));
            } else {
                System.out.println(ConsoleUtils.formatError("Invalid username (cannot be empty or contain spaces)."));
            }
            System.out.print("Choose Username: ");
            username = scanner.nextLine().trim();
        }
        
        System.out.print("Choose Password: ");
        String password = scanner.nextLine().trim();
        while (password.isEmpty()) {
            System.out.print("Password cannot be empty. Choose Password: ");
            password = scanner.nextLine().trim();
        }
        
        userManager.registerUser(firstName, lastName, username, password); 
        System.out.println(ConsoleUtils.formatSuccess("Account created successfully! Please login."));
    }
}
