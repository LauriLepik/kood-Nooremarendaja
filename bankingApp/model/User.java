package model;

import java.math.BigDecimal;

public class User {
    private final String firstName;
    private final String lastName;
    private final String username;
    private String password;
    private BigDecimal cash;
    private final SavingsAccount savingsAccount;
    private final InvestmentAccount investmentAccount;
    private java.time.LocalDateTime lastLogin;
    private boolean hasFraudWarning = false;
    private boolean isFrozen = false;
    private String iban;
    private final java.util.List<Transaction> transactionHistory = new java.util.ArrayList<>();

    public User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cash = new BigDecimal("10000.00");
        this.savingsAccount = new SavingsAccount();
        this.investmentAccount = new InvestmentAccount();
        this.iban = util.IbanGenerator.generateIban();
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
    
    public String getName() {
        return firstName + (lastName.isEmpty() ? "" : " " + lastName);
    }
    
    public String getPassword() {
        return password;
    }

    public java.time.LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(java.time.LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean hasFraudWarning() {
        return hasFraudWarning;
    }

    public void setFraudWarning(boolean hasFraudWarning) {
        this.hasFraudWarning = hasFraudWarning;
    }
    
    public boolean isFrozen() {
        return isFrozen;
    }
    
    public void setFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    public InvestmentAccount getInvestmentAccount() {
        return investmentAccount;
    }

    public void addCash(BigDecimal amount) {
        this.cash = this.cash.add(amount);
    }

    public void subtractCash(BigDecimal amount) {
        this.cash = this.cash.subtract(amount);
    }

    public java.util.List<Transaction> getTransactionHistory() {
        return new java.util.ArrayList<>(transactionHistory);
    }
    
    public void addTransaction(Transaction t) {
        transactionHistory.add(t);
    }
}
