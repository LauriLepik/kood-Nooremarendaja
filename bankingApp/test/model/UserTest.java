package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    
    private User user;
    
    @BeforeEach
    void setUp() {
        user = new User("John", "Doe", "johndoe", "password123");
    }
    
    @Test
    void testUserCreationWithAllFields() {
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("password123", user.getPassword());
    }
    
    @Test
    void testGetNameWithLastName() {
        assertEquals("John Doe", user.getName());
    }
    
    @Test
    void testGetNameWithoutLastName() {
        User userNoLast = new User("Alice", "", "alice", "pass");
        assertEquals("Alice", userNoLast.getName());
    }
    
    @Test
    void testInitialCashBalanceIs10000() {
        assertEquals(new BigDecimal("10000.00"), user.getCash());
    }
    
    @Test
    void testInitialAccountsAreCreated() {
        assertNotNull(user.getSavingsAccount());
        assertNotNull(user.getInvestmentAccount());
        assertEquals(BigDecimal.ZERO, user.getSavingsAccount().getBalance());
        assertEquals(BigDecimal.ZERO, user.getInvestmentAccount().getBalance());
    }
    
    @Test
    void testIbanIsGenerated() {
        assertNotNull(user.getIban());
        assertTrue(user.getIban().startsWith("EE"));
        assertEquals(12, user.getIban().length());
    }
    
    @Test
    void testAddCash() {
        user.addCash(new BigDecimal("500.00"));
        assertEquals(new BigDecimal("10500.00"), user.getCash());
    }
    
    @Test
    void testSubtractCash() {
        user.subtractCash(new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("9000.00"), user.getCash());
    }
    
    @Test
    void testFraudWarningFlag() {
        assertFalse(user.hasFraudWarning());
        
        user.setFraudWarning(true);
        assertTrue(user.hasFraudWarning());
        
        user.setFraudWarning(false);
        assertFalse(user.hasFraudWarning());
    }
    
    @Test
    void testFrozenFlag() {
        assertFalse(user.isFrozen());
        
        user.setFrozen(true);
        assertTrue(user.isFrozen());
        
        user.setFrozen(false);
        assertFalse(user.isFrozen());
    }
    
    @Test
    void testLastLogin() {
        assertNull(user.getLastLogin());
        
        LocalDateTime loginTime = LocalDateTime.now();
        user.setLastLogin(loginTime);
        assertEquals(loginTime, user.getLastLogin());
    }
    
    @Test
    void testTransactionHistoryTracking() {
        assertEquals(0, user.getTransactionHistory().size());
        
        Transaction transaction = new Transaction(
            LocalDateTime.now(),
            Transaction.Type.DEPOSIT,
            new BigDecimal("100.00"),
            "Test transaction",
            new BigDecimal("100.00")
        );
        
        user.addTransaction(transaction);
        
        assertEquals(1, user.getTransactionHistory().size());
        assertEquals(transaction, user.getTransactionHistory().get(0));
    }
    
    @Test
    void testGetTransactionHistoryReturnsCopy() {
        Transaction transaction = new Transaction(
            LocalDateTime.now(),
            Transaction.Type.DEPOSIT,
            new BigDecimal("50.00"),
            "Test",
            new BigDecimal("50.00")
        );
        
        user.addTransaction(transaction);
        var history = user.getTransactionHistory();
        history.clear();
        
        // Original should still have the transaction
        assertEquals(1, user.getTransactionHistory().size());
    }
}
