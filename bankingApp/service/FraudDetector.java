package service;

import model.Transaction;
import model.User;

public class FraudDetector {
    private static final java.math.BigDecimal LARGE_TRANSACTION_LIMIT = new java.math.BigDecimal("10000.00");

    public boolean isSuspicious(Transaction transaction) {
        if (transaction.getAmount().compareTo(LARGE_TRANSACTION_LIMIT) > 0) {
            return true;
        }
        return false;
    }
    public boolean shouldFreezeAccount(User user, java.time.LocalDateTime currentTime) {
        long suspiciousCount = user.getTransactionHistory().stream()
            .filter(t -> t.getType() == Transaction.Type.TRANSFER || t.getType() == Transaction.Type.WITHDRAW)
            .filter(t -> isSuspicious(t))
            .filter(t -> java.time.temporal.ChronoUnit.HOURS.between(t.getTimestamp(), currentTime) < 24)
            .count();
            
        return suspiciousCount >= 3;
    }
}
