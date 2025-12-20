package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import model.Transaction;
import model.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class FraudDetectorTest {
    
    private FraudDetector fraudDetector;
    private User user;
    
    @BeforeEach
    void setUp() {
        fraudDetector = new FraudDetector();
        user = new User("Test", "User", "testuser", "password");
    }
    
    @Test
    void testIsSuspiciousForLargeTransaction() {
        Transaction largeTransaction = new Transaction(
            LocalDateTime.now(),
            Transaction.Type.TRANSFER,
            new BigDecimal("15000.00"),
            "Large transfer",
            user.getCash()
        );
        
        assertTrue(fraudDetector.isSuspicious(largeTransaction));
    }
    
    @Test
    void testIsSuspiciousForSmallTransaction() {
        Transaction smallTransaction = new Transaction(
            LocalDateTime.now(),
            Transaction.Type.TRANSFER,
            new BigDecimal("5000.00"),
            "Small transfer",
            user.getCash()
        );
        
        assertFalse(fraudDetector.isSuspicious(smallTransaction));
    }
    
    @Test
    void testIsSuspiciousAtExactLimit() {
        Transaction limitTransaction = new Transaction(
            LocalDateTime.now(),
            Transaction.Type.TRANSFER,
            new BigDecimal("10000.00"),
            "Limit transfer",
            user.getCash()
        );
        
        assertFalse(fraudDetector.isSuspicious(limitTransaction));
    }
    
    @Test
    void testShouldFreezeAccountWithThreeSuspiciousTransactions() {
        LocalDateTime now = LocalDateTime.now();
        
        // Add 3 suspicious transactions within 24 hours
        for (int i = 0; i < 3; i++) {
            Transaction large = new Transaction(
                i + "-id",
                now.minusHours(i),
                Transaction.Type.TRANSFER,
                new BigDecimal("11000.00"),
                "Large " + i,
                user.getCash()
            );
            user.addTransaction(large);
        }
        
        assertTrue(fraudDetector.shouldFreezeAccount(user, now));
    }
    
    @Test
    void testShouldNotFreezeAccountWithTwoSuspiciousTransactions() {
        LocalDateTime now = LocalDateTime.now();
        
        // Add only 2 suspicious transactions
        for (int i = 0; i < 2; i++) {
            Transaction large = new Transaction(
                i + "-id",
                now.minusHours(i),
                Transaction.Type.TRANSFER,
                new BigDecimal("11000.00"),
                "Large " + i,
                user.getCash()
            );
            user.addTransaction(large);
        }
        
        assertFalse(fraudDetector.shouldFreezeAccount(user, now));
    }
    
    @Test
    void testShouldNotFreezeAccountWithOldSuspiciousTransactions() {
        LocalDateTime now = LocalDateTime.now();
        
        // Add 3 suspicious transactions but older than 24 hours
        for (int i = 0; i < 3; i++) {
            Transaction large = new Transaction(
                i + "-id",
                now.minusHours(25 + i),
                Transaction.Type.TRANSFER,
                new BigDecimal("11000.00"),
                "Old large " + i,
                user.getCash()
            );
            user.addTransaction(large);
        }
        
        assertFalse(fraudDetector.shouldFreezeAccount(user, now));
    }
    
    @Test
    void testOnlyTransferAndWithdrawTypesCountForFreezing() {
        LocalDateTime now = LocalDateTime.now();
        
        // Add 3 suspicious deposits (should not count)
        for (int i = 0; i < 3; i++) {
            Transaction deposit = new Transaction(
                i + "-id",
                now.minusHours(i),
                Transaction.Type.DEPOSIT,
                new BigDecimal("11000.00"),
                "Large deposit " + i,
                user.getCash()
            );
            user.addTransaction(deposit);
        }
        
        assertFalse(fraudDetector.shouldFreezeAccount(user, now));
    }
}
