package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public enum Type {
        DEPOSIT, WITHDRAW, TRANSFER, INVEST, GAIN_APPLIED
    }

    private final String id;
    private final LocalDateTime timestamp;
    private final Type type;
    private final BigDecimal amount;
    private final String description;
    private final BigDecimal balanceAfter;

    public Transaction(LocalDateTime timestamp, Type type, BigDecimal amount, String description, BigDecimal balanceAfter) {
        this(java.util.UUID.randomUUID().toString(), timestamp, type, amount, description, balanceAfter);
    }
    
    public Transaction(String id, LocalDateTime timestamp, Type type, BigDecimal amount, String description, BigDecimal balanceAfter) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.balanceAfter = balanceAfter;
    }
    
    public String getId() { return id; }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s | %-12s | %-30s | $%-10s | Bal: $%s",
            timestamp.format(formatter), type, description, amount, balanceAfter);
    }
}
