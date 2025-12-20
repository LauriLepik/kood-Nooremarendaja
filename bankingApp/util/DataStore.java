package util;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import model.Transaction;
import model.User;

public class DataStore {
    private final String dbDir;
    private final String usersFile;
    private final String transactionsFile;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Default constructor for production use
    public DataStore() {
        this("database");
    }
    
    // Constructor for testing
    public DataStore(String databaseDirectory) {
        this.dbDir = databaseDirectory;
        this.usersFile = dbDir + "/users.csv";
        this.transactionsFile = dbDir + "/transactions.csv";
    }

    public Map<String, User> loadData() {
        Map<String, User> users = new HashMap<>();
        ensureDatabaseDirectory();

        File uFile = new File(usersFile);
        if (!uFile.exists()) {
            return users;
        }

        int lineNumber = 0;
        int successfullyLoaded = 0;
        int skippedRecords = 0;

        // Load Users
        try (BufferedReader br = new BufferedReader(new FileReader(uFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Wrap each record in try-catch to skip corrupted records
                try {
                    String[] parts = line.split(",");
                    // Schema: username,encrypted_password,firstName,lastName,cash,savings,investment,lastLogin,hasFraudWarning,isFrozen,iban

                    // Validate minimum required fields
                    if (parts.length < 7) {
                        System.err.println("Error at line " + lineNumber + ": Insufficient fields (expected at least 7, got " + parts.length + "). Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    // Validate required fields are not empty
                    String username = parts[0].trim();
                    String encPass = parts[1].trim();
                    String firstName = parts[2].trim();
                    String lastName = parts[3].trim();

                    if (username.isEmpty()) {
                        System.err.println("Error at line " + lineNumber + ": Username is empty. Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    if (encPass.isEmpty()) {
                        System.err.println("Error at line " + lineNumber + ": Encrypted password is empty for user '" + username + "'. Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    if (firstName.isEmpty()) {
                        System.err.println("Error at line " + lineNumber + ": First name is empty for user '" + username + "'. Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    // Parse and validate BigDecimal fields
                    BigDecimal cash;
                    BigDecimal savings;
                    BigDecimal investment;

                    try {
                        cash = new BigDecimal(parts[4].trim());
                        if (cash.compareTo(BigDecimal.ZERO) < 0) {
                            System.err.println("Error at line " + lineNumber + ": Cash amount is negative for user '" + username + "'. Skipping record.");
                            skippedRecords++;
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error at line " + lineNumber + ": Invalid cash amount '" + parts[4] + "' for user '" + username + "'. Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    try {
                        savings = new BigDecimal(parts[5].trim());
                        if (savings.compareTo(BigDecimal.ZERO) < 0) {
                            System.err.println("Error at line " + lineNumber + ": Savings amount is negative for user '" + username + "'. Skipping record.");
                            skippedRecords++;
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error at line " + lineNumber + ": Invalid savings amount '" + parts[5] + "' for user '" + username + "'. Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    try {
                        investment = new BigDecimal(parts[6].trim());
                        if (investment.compareTo(BigDecimal.ZERO) < 0) {
                            System.err.println("Error at line " + lineNumber + ": Investment amount is negative for user '" + username + "'. Skipping record.");
                            skippedRecords++;
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error at line " + lineNumber + ": Invalid investment amount '" + parts[6] + "' for user '" + username + "'. Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    // Attempt to decrypt password
                    String decryptedPassword;
                    try {
                        decryptedPassword = SecurityUtils.decrypt(encPass);
                        if (decryptedPassword == null || decryptedPassword.isEmpty()) {
                            System.err.println("Error at line " + lineNumber + ": Decrypted password is empty for user '" + username + "'. Skipping record.");
                            skippedRecords++;
                            continue;
                        }
                    } catch (Exception e) {
                        System.err.println("Error at line " + lineNumber + ": Failed to decrypt password for user '" + username + "': " + e.getMessage() + ". Skipping record.");
                        skippedRecords++;
                        continue;
                    }

                    // Create user
                    User u = new User(firstName, lastName, username, decryptedPassword);

                    // Parse optional lastLogin field
                    if (parts.length >= 8 && !parts[7].trim().equals("null") && !parts[7].trim().isEmpty()) {
                        try {
                            u.setLastLogin(LocalDateTime.parse(parts[7].trim(), DATE_FMT));
                        } catch (Exception e) {
                            System.err.println("Warning at line " + lineNumber + ": Invalid date format '" + parts[7] + "' for user '" + username + "'. Using null for lastLogin.");
                        }
                    }

                    // Parse optional hasFraudWarning field
                    if (parts.length >= 9 && !parts[8].trim().isEmpty()) {
                        try {
                            u.setFraudWarning(Boolean.parseBoolean(parts[8].trim()));
                        } catch (Exception e) {
                            System.err.println("Warning at line " + lineNumber + ": Invalid boolean '" + parts[8] + "' for hasFraudWarning. Defaulting to false.");
                            u.setFraudWarning(false);
                        }
                    }

                    // Parse optional isFrozen field
                    if (parts.length >= 10 && !parts[9].trim().isEmpty()) {
                        try {
                            u.setFrozen(Boolean.parseBoolean(parts[9].trim()));
                        } catch (Exception e) {
                            System.err.println("Warning at line " + lineNumber + ": Invalid boolean '" + parts[9] + "' for isFrozen. Defaulting to false.");
                            u.setFrozen(false);
                        }
                    }

                    // Parse optional IBAN field
                    if (parts.length >= 11 && !parts[10].trim().isEmpty()) {
                        u.setIban(parts[10].trim());
                    } else {
                        u.setIban(util.IbanGenerator.generateIban());
                    }

                    // Set account balances
                    u.subtractCash(u.getCash());
                    u.addCash(cash);
                    u.getSavingsAccount().deposit(savings);
                    u.getInvestmentAccount().deposit(investment);

                    users.put(username.toLowerCase(), u);
                    successfullyLoaded++;

                } catch (Exception e) {
                    System.err.println("Error at line " + lineNumber + ": Unexpected error while parsing user record: " + e.getMessage() + ". Skipping record.");
                    skippedRecords++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users file: " + e.getMessage());
        }

        // Report loading summary
        if (successfullyLoaded > 0 || skippedRecords > 0) {
            System.out.println("User loading complete: " + successfullyLoaded + " loaded, " + skippedRecords + " skipped.");
        }


        // Load Transactions
        File tFile = new File(transactionsFile);
        if (tFile.exists()) {
            int transactionLineNumber = 0;
            int transactionsLoaded = 0;
            int transactionsSkipped = 0;
            
            try (BufferedReader br = new BufferedReader(new FileReader(tFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    transactionLineNumber++;
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Wrap each transaction in try-catch to skip corrupted records
                    try {
                        String[] parts = line.split(",");
                        // Schema: id,timestamp,amount,description,sender,receiver,senderBal,receiverBal
                        
                        // Validate minimum required fields
                        if (parts.length < 8) {
                            System.err.println("Error at transaction line " + transactionLineNumber + ": Insufficient fields (expected 8, got " + parts.length + "). Skipping transaction.");
                            transactionsSkipped++;
                            continue;
                        }
                        
                        String id = parts[0].trim();
                        if (id.isEmpty()) {
                            System.err.println("Error at transaction line " + transactionLineNumber + ": Transaction ID is empty. Skipping transaction.");
                            transactionsSkipped++;
                            continue;
                        }
                        
                        // Parse timestamp
                        LocalDateTime timestamp;
                        try {
                            timestamp = LocalDateTime.parse(parts[1].trim(), DATE_FMT);
                        } catch (Exception e) {
                            System.err.println("Error at transaction line " + transactionLineNumber + ": Invalid timestamp format '" + parts[1] + "'. Skipping transaction.");
                            transactionsSkipped++;
                            continue;
                        }
                        
                        // Parse amount
                        BigDecimal amount;
                        try {
                            amount = new BigDecimal(parts[2].trim());
                            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                                System.err.println("Error at transaction line " + transactionLineNumber + ": Transaction amount is negative. Skipping transaction.");
                                transactionsSkipped++;
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error at transaction line " + transactionLineNumber + ": Invalid amount '" + parts[2] + "'. Skipping transaction.");
                            transactionsSkipped++;
                            continue;
                        }
                        
                        String senderName = parts[4].trim();
                        String receiverName = parts[5].trim();
                        
                        // Parse sender balance
                        BigDecimal senderBal;
                        try {
                            senderBal = new BigDecimal(parts[6].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Error at transaction line " + transactionLineNumber + ": Invalid sender balance '" + parts[6] + "'. Skipping transaction.");
                            transactionsSkipped++;
                            continue;
                        }
                        
                        // Parse receiver balance
                        BigDecimal receiverBal;
                        try {
                            receiverBal = new BigDecimal(parts[7].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Error at transaction line " + transactionLineNumber + ": Invalid receiver balance '" + parts[7] + "'. Skipping transaction.");
                            transactionsSkipped++;
                            continue;
                        }
                        
                        boolean transactionAdded = false;
                        
                        // Reconstruct Sender Transaction
                        if (!senderName.equals("null") && !senderName.isEmpty()) {
                            User sender = users.get(senderName.toLowerCase());
                            if (sender != null) {
                                Transaction tSender = new Transaction(id, timestamp, Transaction.Type.TRANSFER, amount, "Sent to " + (receiverName.equals("null") ? "External" : receiverName), senderBal);
                                sender.addTransaction(tSender);
                                transactionAdded = true;
                            } else {
                                System.err.println("Warning at transaction line " + transactionLineNumber + ": Sender user '" + senderName + "' not found (orphaned transaction). Skipping sender side.");
                            }
                        }
                        
                        // Reconstruct Receiver Transaction
                        if (!receiverName.equals("null") && !receiverName.isEmpty()) {
                            User receiver = users.get(receiverName.toLowerCase());
                            if (receiver != null) {
                                Transaction tReceiver = new Transaction(id, timestamp, Transaction.Type.DEPOSIT, amount, "Received from " + (senderName.equals("null") ? "External" : senderName), receiverBal);
                                receiver.addTransaction(tReceiver);
                                transactionAdded = true;
                            } else {
                                System.err.println("Warning at transaction line " + transactionLineNumber + ": Receiver user '" + receiverName + "' not found (orphaned transaction). Skipping receiver side.");
                            }
                        }
                        
                        if (transactionAdded) {
                            transactionsLoaded++;
                        } else {
                            transactionsSkipped++;
                        }
                        
                    } catch (Exception e) {
                        System.err.println("Error at transaction line " + transactionLineNumber + ": Unexpected error while parsing transaction: " + e.getMessage() + ". Skipping transaction.");
                        transactionsSkipped++;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading transactions file: " + e.getMessage());
            }
            
            // Report transaction loading summary
            if (transactionsLoaded > 0 || transactionsSkipped > 0) {
                System.out.println("Transaction loading complete: " + transactionsLoaded + " loaded, " + transactionsSkipped + " skipped.");
            }
        }

        return users;
    }

    public void saveData(Map<String, User> users) {
        ensureDatabaseDirectory();

        // Save Users
        try (PrintWriter pw = new PrintWriter(new FileWriter(usersFile))) {
            for (User u : users.values()) {
                // Formatting: username,encrypted_password,firstName,lastName,cash,savings,investment,lastLogin,hasFraudWarning,isFrozen,iban
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        u.getUsername(),
                        SecurityUtils.encrypt(u.getPassword()),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getCash(),
                        u.getSavingsAccount().getBalance(),
                        u.getInvestmentAccount().getBalance(),
                        u.getLastLogin() != null ? u.getLastLogin().format(DATE_FMT) : "null",
                        u.hasFraudWarning(),
                        u.isFrozen(),
                        u.getIban()
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }

        // Save Transactions
        // Group by ID to deduplicate shared transactions for the single CSV
        Map<String, List<TransactionContext>> grouped = new HashMap<>();

        for (User u : users.values()) {
            for (Transaction t : u.getTransactionHistory()) {
                grouped.computeIfAbsent(t.getId(), k -> new ArrayList<>()).add(new TransactionContext(u.getUsername(), t));
            }
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(transactionsFile))) {
            for (Map.Entry<String, List<TransactionContext>> entry : grouped.entrySet()) {
                String id = entry.getKey();
                List<TransactionContext> ctxList = entry.getValue();

                // Defaults
                String sender = "null";
                String receiver = "null";
                BigDecimal senderBal = BigDecimal.ZERO;
                BigDecimal receiverBal = BigDecimal.ZERO;
                BigDecimal amount = BigDecimal.ZERO;
                LocalDateTime ts = LocalDateTime.now();
                String desc = "";

                for (TransactionContext ctx : ctxList) {
                    Transaction t = ctx.t;
                    ts = t.getTimestamp();
                    amount = t.getAmount();
                    desc = t.getDescription();

                    if (t.getType() == Transaction.Type.TRANSFER || t.getType() == Transaction.Type.WITHDRAW) {
                        sender = ctx.username;
                        senderBal = t.getBalanceAfter();
                    } else if (t.getType() == Transaction.Type.DEPOSIT) {
                        receiver = ctx.username;
                        receiverBal = t.getBalanceAfter();
                    }
                }

                // id,timestamp,amount,desc,sender,receiver,senderBal,receiverBal
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        id,
                        ts.format(DATE_FMT),
                        amount,
                        desc,
                        sender,
                        receiver,
                        senderBal,
                        receiverBal
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    // Helper class
    private static class TransactionContext {
        String username;
        Transaction t;
        public TransactionContext(String username, Transaction t) {
            this.username = username;
            this.t = t;
        }
    }

    private void ensureDatabaseDirectory() {
        File dir = new File(dbDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
