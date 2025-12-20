package service;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.math.BigDecimal;
import java.util.Scanner;
import model.Fund;
import model.InvestmentAccount;
import model.User;
import util.ConsoleUtils;

public class BankingService {
    private final Scanner scanner = new Scanner(System.in);
    private final UserManager userManager = new UserManager();
    private final AuthenticationService authService;
    private final TimeManager timeManager = new TimeManager(userManager);
    private final InputValidator inputValidator;
    private final FraudDetector fraudDetector = new FraudDetector();
    private final TransferService transferService;
    private User currentUser = null;

    public BankingService() {
        this.inputValidator = new InputValidator(scanner);
        this.authService = new AuthenticationService(userManager, scanner);
        this.transferService = new TransferService(userManager, timeManager, fraudDetector, inputValidator, scanner);
    }

    public void start() {
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                currentUser = authService.authenticate();
                if (currentUser == null) {
                    break;  // Exit selected
                }
                
                // Check for real-time catch-up
                if (currentUser.getLastLogin() != null) {
                    long days = timeManager.calculateDaysPassed(currentUser.getLastLogin(), java.time.LocalDateTime.now());
                    if (days > 0) {
                        System.out.println(util.ConsoleUtils.formatSuccess("Welcome back! It's been " + days + " days since your last login."));
                        System.out.println("Processing daily interest and investment gains for missed days...");
                        for (int i = 0; i < days; i++) {
                            timeManager.applyDailyGains(currentUser);
                        }
                    }
                } else {
                    currentUser.setLastLogin(java.time.LocalDateTime.now()); 
                }
                // Update last login to now
                currentUser.setLastLogin(java.time.LocalDateTime.now());
                userManager.save();
                
                continue;
            }
            
            if (currentUser.isFrozen()) {
                util.ConsoleUtils.printAccountFrozenBanner();
                currentUser = null; // Logout immediately
                continue;
            }
            
            if (currentUser.hasFraudWarning()) {
                System.out.println(util.ConsoleUtils.formatError("\n!!! UNCONFIRMED LARGE TRANSFER DETECTED !!!"));
                System.out.println(util.ConsoleUtils.formatError("Please go to 'About Fraud Prevention' to dismiss this warning."));
            }

            printMenu();
            System.out.print("Enter your choice: ");
            if (!scanner.hasNextLine()) break;
            String choice = scanner.nextLine().toLowerCase();
            
            switch (choice) {
                case "1", "show balance" -> showBalance();
                case "2", "deposit money" -> depositMoney();
                case "3", "withdraw money" -> withdrawMoney();
                case "4", "send money" -> {
                    if (!transferService.sendMoney(currentUser)) {
                        currentUser = null; // User frozen, logout
                    }
                }
                case "5", "invest in funds" -> investInFunds();
                case "6", "transfer between accounts" -> transferService.transferBetweenAccounts(currentUser);
                case "7", "withdraw all investments" -> withdrawAllInvestments();
                case "8", "history" -> viewTransactionHistory();
                case "9", "logout", "l" -> logout();
                case "10", "advance time", "skip day" -> advanceTime();
                case "11", "exit", "e" -> {
                    exit();
                    running = false;
                }
                case "12", "fraud", "about fraud prevention" -> aboutFraudPrevention();
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n--- Banking App Menu ---");
        System.out.println("Date: " + timeManager.getCurrentFormattedTime());
        
        if (currentUser != null && currentUser.hasFraudWarning()) {
             System.out.println(util.ConsoleUtils.formatError("[!] ACTION REQUIRED: Review Fraud Warning [!]"));
        }
        System.out.println("------------------------");
        System.out.println("1. Show balance");
        System.out.println("2. Deposit money");
        System.out.println("3. Withdraw money");
        System.out.println("4. Send money to a person");
        System.out.println("5. Invest in funds");
        System.out.println("6. Transfer between accounts");
        System.out.println("7. Withdraw all investments");
        System.out.println("8. View Transaction History");
        System.out.println("9. Logout");
        System.out.println("10. Advance Time (1 Day)");
        System.out.println("11. Exit");
        if (currentUser != null && currentUser.hasFraudWarning()) {
            System.out.println("12. " + util.ConsoleUtils.formatError("Dismiss Fraud Warning"));
        } else {
            System.out.println("12. About Fraud Prevention");
        }
    }
    
    private void aboutFraudPrevention() {
        if (currentUser.hasFraudWarning()) {
            System.out.println("\n--- FRAUD WARNING ---");
            System.out.println("A recent transaction exceeded the security limit of $10,000.");
            System.out.println("This has been flagged for your review.");
            System.out.print("Do you recognize this activity? (yes/no): ");

            System.out.println("\nPress 'D' to Dismiss this warning.");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("D")) {
                currentUser.setFraudWarning(false);
                userManager.save();
                System.out.println("Warning dismissed. Thank you for verifying.");
            }
        } else {
            System.out.println("\n--- About Fraud Prevention ---");
            System.out.println("Here at Green Day Bank we take fraud very seriously.");
            System.out.println("You will be flagged if a transaction exceeds $10,000.");
            System.out.println("The fraud detection kicks in after 3 over $10,000 transfers out of the account per 24 hours.");
            System.out.println("This leads to automatic account suspension.");
            System.out.println("Press Enter to return...");
            scanner.nextLine();
        }
    }

    private void advanceTime() {
        System.out.println("Simulating 1 day passing...");
        timeManager.advanceTime(1);
        System.out.println(ConsoleUtils.formatSuccess("Time advanced! Interest and investment gains applied."));
    }
    
    private void showBalance() {
        currentUser.getSavingsAccount().applyGains();
        currentUser.getInvestmentAccount().applyGains();
        
        System.out.println("--- Your Balance ---");
        System.out.println("IBAN: " + currentUser.getIban());
        System.out.println("Savings account balance: " + ConsoleUtils.formatMoney(currentUser.getSavingsAccount().getBalance().setScale(2)));
        System.out.println("Investment account balance:");
        InvestmentAccount inv = currentUser.getInvestmentAccount();
        System.out.println("* Not Invested: " + ConsoleUtils.formatMoney(inv.getBalance().setScale(2)));
        
        for (Fund fund : inv.getFundsEverInvested()) {
            System.out.println("* " + fund.name() + ": $" + inv.getFundBalance(fund).setScale(2));
        }
    }
    
    private void depositMoney() {
        System.out.print("Enter amount to deposit to savings account: $");
        
        try {
            BigDecimal amount = inputValidator.readAmount();
            if (amount == null) {
                return;  // Non-numeric input - silently return to menu
            }
            
            if (amount.compareTo(currentUser.getCash()) > 0) {
                throw new InsufficientFundsException("Insufficient cash on hand");
            }
            
            currentUser.subtractCash(amount);
            currentUser.getSavingsAccount().deposit(amount);
            
            currentUser.addTransaction(new model.Transaction(
                timeManager.getCurrentTime(),
                model.Transaction.Type.DEPOSIT,
                amount,
                "Cash Deposit",
                currentUser.getSavingsAccount().getBalance()
            ));
            
            System.out.println(ConsoleUtils.formatSuccess("Deposit successful."));
            
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println(ConsoleUtils.formatError("Deposit failed: " + e.getMessage()));
        }
    }
    
    private void withdrawMoney() {
        System.out.print("Enter amount to withdraw from savings account: $");
        
        try {
            BigDecimal amount = inputValidator.readAmount();
            if (amount == null) {
                return;  // Non-numeric input - silently return to menu
            }
            
            if (amount.compareTo(currentUser.getSavingsAccount().getBalance()) > 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
            
            currentUser.getSavingsAccount().withdraw(amount);
            currentUser.addCash(amount);
            
            currentUser.addTransaction(new model.Transaction(
                timeManager.getCurrentTime(),
                model.Transaction.Type.WITHDRAW,
                amount,
                "Cash Withdrawal",
                currentUser.getSavingsAccount().getBalance()
            ));

            System.out.println(ConsoleUtils.formatSuccess("Withdrawal successful."));
            
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println(ConsoleUtils.formatError("Withdrawal failed: " + e.getMessage()));
        }
    }
    
    private void investInFunds() {
        System.out.println("Available funds:");
        System.out.println("LOW_RISK");
        System.out.println("MEDIUM_RISK");
        System.out.println("HIGH_RISK");
        System.out.print("Enter fund to invest in: ");
        if (!scanner.hasNextLine()) return;
        String fundName = scanner.nextLine().trim();
        
        Fund fund = findFund(fundName);
        if (fund == null) {
            System.out.println("Invalid fund.");
            return;
        }
        
        System.out.print("Enter amount to invest: $");
        
        try {
            BigDecimal amount = inputValidator.readAmount();
            if (amount == null) {
                return;  // Non-numeric input - silently return to menu
            }
            
            InvestmentAccount inv = currentUser.getInvestmentAccount();
            if (amount.compareTo(inv.getBalance()) > 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
            
            inv.investInFund(fund, amount);
            System.out.println("Successfully invested $" + amount + " in " + fund.name() + " fund");
            
            currentUser.addTransaction(new model.Transaction(
                timeManager.getCurrentTime(),
                model.Transaction.Type.INVEST,
                amount,
                "Invested in " + fund.name(),
                inv.getBalance()
            ));
            
        } catch (InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Failed to invest: " + e.getMessage());
        }
    }
    
    private Fund findFund(String name) {
        for (Fund fund : Fund.values()) {
            if (fund.name().equalsIgnoreCase(name)) {
                return fund;
            }
        }
        return null;
    }
    
    private void withdrawAllInvestments() {
        currentUser.getInvestmentAccount().withdrawAllFunds();
        System.out.println("All investments have been withdrawn and added to your investment account balance.");
    }
    
    private void logout() {
        System.out.println("You have been logged out.");
        currentUser = null;
    }

    private void exit() {
        System.out.println("Thank you for using our banking app. Goodbye!");
    }
    private void viewTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        java.util.List<model.Transaction> history = currentUser.getTransactionHistory();
        
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("Timestamp           | Type         | Description                    | Amount     | Balance");
            System.out.println("------------------------------------------------------------------------------------------");
            for (model.Transaction t : history) {
                System.out.println(t);
            }
        }
    }
}
