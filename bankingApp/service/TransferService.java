package service;

import model.User;
import util.ConsoleUtils;
import exception.InvalidAmountException;
import exception.InsufficientFundsException;
import java.math.BigDecimal;
import java.util.Scanner;

public class TransferService {
    private final UserManager userManager;
    private final TimeManager timeManager;
    private final FraudDetector fraudDetector;
    private final InputValidator inputValidator;
    private final Scanner scanner;

    public TransferService(UserManager userManager, TimeManager timeManager, FraudDetector fraudDetector, InputValidator inputValidator, Scanner scanner) {
        this.userManager = userManager;
        this.timeManager = timeManager;
        this.fraudDetector = fraudDetector;
        this.inputValidator = inputValidator;
        this.scanner = scanner;
    }

    public boolean sendMoney(User currentUser) {
        System.out.println("\n--- Send Money ---");
        System.out.println("1. Quick Transfer (Previously Sent)");
        System.out.println("2. Secure Transfer (New Recipient / IBAN)");
        System.out.print("Choose option: ");
        if (!scanner.hasNextLine()) return true;
        String choice = scanner.nextLine().trim();
        
        String recipientUsername = null;
        String message = "";
        
        if (choice.equals("1")) {
             java.util.Set<String> previousRecipients = new java.util.HashSet<>();
             
             for (model.Transaction t : currentUser.getTransactionHistory()) {
                 if (t.getType() == model.Transaction.Type.TRANSFER && t.getDescription().startsWith("Sent to ")) {
                     String desc = t.getDescription();
                     String namePart = desc.substring(8);
                     if (namePart.contains(" - ")) {
                         namePart = namePart.split(" - ")[0];
                     }
                     previousRecipients.add(namePart.toLowerCase());
                 }
             }
             
             System.out.println("Previously messaged recipients:");
             int count = 1;
             java.util.List<String> validUsernames = new java.util.ArrayList<>();
             
             for (User u : userManager.getAllUsers().values()) {
                 if (!u.getUsername().equals(currentUser.getUsername()) && previousRecipients.contains(u.getName().toLowerCase())) {
                     System.out.println(count + ". " + u.getName() + " (" + u.getUsername() + ")");
                     validUsernames.add(u.getUsername());
                     count++;
                 }
             }
             
             if (validUsernames.isEmpty()) {
                 System.out.println("No previous recipients found. Please use Secure Transfer to add new people.");
                 return true;
             }
             
             System.out.print("Enter number: ");
             if (!scanner.hasNextLine()) return true;
             String input = scanner.nextLine().trim();
             try {
                int selection = Integer.parseInt(input);
                if (selection >= 1 && selection <= validUsernames.size()) {
                    recipientUsername = validUsernames.get(selection - 1);
                } else {
                    System.out.println("Invalid selection.");
                    return true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                return true;
            }

        } else if (choice.equals("2")) {
            System.out.print("Enter Recipient Full Name: ");
            if (!scanner.hasNextLine()) return true;
            String fullName = scanner.nextLine().trim();
            
            System.out.print("Enter Recipient IBAN: ");
            if (!scanner.hasNextLine()) return true;
            String ibanInput = scanner.nextLine().trim();
            
            User recipient = null;
            for (User u : userManager.getAllUsers().values()) {
                if (u.getIban() != null && u.getIban().equalsIgnoreCase(ibanInput)) {
                    recipient = u;
                    break;
                }
            }
            
            if (recipient == null) {
                System.out.println(ConsoleUtils.formatError("Invalid IBAN. User not found."));
                return true;
            }
            
            if (!recipient.getName().equalsIgnoreCase(fullName)) {
                System.out.println(ConsoleUtils.formatError("Security Check Failed: Name does not match IBAN."));
                return true;
            }
            
            recipientUsername = recipient.getUsername();
            System.out.print("Enter Message: ");
            if (!scanner.hasNextLine()) return true;
            message = scanner.nextLine().trim();
            
        } else {
            System.out.println("Invalid option.");
            return true;
        }
        
        if (recipientUsername == null) {
            System.out.println(ConsoleUtils.formatError("Invalid recipient."));
            return true;
        }
        
        System.out.print("Enter amount to send: $");
        
        try {
            BigDecimal amount = inputValidator.readAmount();
            if (amount == null) return true;
            
            if (amount.compareTo(currentUser.getCash()) > 0) {
                throw new InsufficientFundsException("Insufficient cash.");
            }
            
            model.Transaction potentialTrans = new model.Transaction(
                java.util.UUID.randomUUID().toString(),
                timeManager.getCurrentTime(),
                model.Transaction.Type.TRANSFER,
                amount,
                "Pending Check",
                currentUser.getCash()
            );
            
            if (fraudDetector.isSuspicious(potentialTrans)) {
                currentUser.setFraudWarning(true);
                System.out.println(ConsoleUtils.formatError("WARNING: Large transaction detected!"));
                
                if (fraudDetector.shouldFreezeAccount(currentUser, timeManager.getCurrentTime())) {
                    currentUser.setFrozen(true);
                    userManager.save();
                    ConsoleUtils.printAccountFrozenBanner();
                    return false;
                }
            }

            User recipient = userManager.getAllUsers().get(recipientUsername);
            String transactionId = java.util.UUID.randomUUID().toString();
            
            currentUser.subtractCash(amount);
            String sendDesc = "Sent to " + recipient.getName();
            if (!message.isEmpty()) sendDesc += " - " + message;
            
            currentUser.addTransaction(new model.Transaction(
                transactionId,
                timeManager.getCurrentTime(),
                model.Transaction.Type.TRANSFER,
                amount,
                sendDesc,
                currentUser.getCash()
            ));

            recipient.addCash(amount);
            String rxDesc = "Received from " + currentUser.getName();
            if (!message.isEmpty()) rxDesc += " - " + message;

            recipient.addTransaction(new model.Transaction(
                transactionId,
                timeManager.getCurrentTime(),
                model.Transaction.Type.DEPOSIT,
                amount,
                rxDesc,
                recipient.getCash()
            ));

            userManager.save();
            System.out.println(ConsoleUtils.formatSuccess("Successfully sent $" + amount + " to " + recipient.getName()));
            
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println(ConsoleUtils.formatError("Failed to send money: " + e.getMessage()));
        }
        return true;
    }

    public void transferBetweenAccounts(User currentUser) {
        System.out.println("1. Transfer from savings to investment");
        System.out.println("2. Transfer from investment to savings");
        System.out.print("Enter your choice: ");
        if (!scanner.hasNextLine()) return;
        String direction = scanner.nextLine().trim();
        
        System.out.print("Enter amount to transfer: $");
        
        try {
            BigDecimal amount = inputValidator.readAmount();
            
            boolean toInvestments = direction.equals("1");
            boolean toSavings = direction.equals("2");
            
            if (!toInvestments && !toSavings) {
                System.out.println("Invalid choice.");
                return;
            }

            if (amount == null) {
                return;
            }
            
            String transactionId = java.util.UUID.randomUUID().toString();
            
            if (toInvestments) {
                if (amount.compareTo(currentUser.getSavingsAccount().getBalance()) > 0) {
                    throw new InsufficientFundsException("Insufficient funds");
                }
                currentUser.getSavingsAccount().withdraw(amount);
                currentUser.getInvestmentAccount().deposit(amount);
                System.out.println("Successfully transferred $" + amount + " to investment account.");
                
                currentUser.addTransaction(new model.Transaction(
                    transactionId,
                    timeManager.getCurrentTime(),
                    model.Transaction.Type.TRANSFER,
                    amount,
                    "To Investment Account",
                    currentUser.getSavingsAccount().getBalance()
                ));
            } else {
                if (amount.compareTo(currentUser.getInvestmentAccount().getBalance()) > 0) {
                    throw new InsufficientFundsException("Insufficient funds");
                }
                currentUser.getInvestmentAccount().withdraw(amount);
                currentUser.getSavingsAccount().deposit(amount);
                System.out.println("Successfully transferred $" + amount + " to savings account.");

                currentUser.addTransaction(new model.Transaction(
                    transactionId,
                    timeManager.getCurrentTime(),
                    model.Transaction.Type.TRANSFER,
                    amount,
                    "From Investment Account",
                    currentUser.getSavingsAccount().getBalance()
                ));
            }
            
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
}
