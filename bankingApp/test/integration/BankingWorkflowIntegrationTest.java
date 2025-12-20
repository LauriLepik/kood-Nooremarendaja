package integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import service.*;
import model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for complete user workflows.
 * All tests use in-memory mode for complete isolation.
 */
class BankingWorkflowIntegrationTest {
    
    private UserManager userManager;
    private TimeManager timeManager;
    private FraudDetector fraudDetector;
    
    @BeforeEach
    void setUp() {
        userManager = new UserManager(true);  // In-memory
        timeManager = new TimeManager(userManager);
        fraudDetector = new FraudDetector();
    }
    
    @Test
    void testCompleteNewUserWorkflow() {
        userManager.registerUser("Alice", "Johnson", "alice", "password123");
        User alice = userManager.findUser("alice");
        
        assertNotNull(alice);
        assertEquals(new BigDecimal("10000.00"), alice.getCash());
        
        // Make deposit
        alice.subtractCash(new BigDecimal("5000.00"));
        alice.getSavingsAccount().deposit(new BigDecimal("5000.00"));
        
        assertEquals(new BigDecimal("5000.00"), alice.getCash());
        assertEquals(new BigDecimal("5000.00"), alice.getSavingsAccount().getBalance());
    }
    
    @Test
    void testCompleteTransferBetweenTwoUsers() {
        userManager.registerUser("Bob", "Smith", "bob", "pass1");
        userManager.registerUser("Carol", "White", "carol", "pass2");
        
        User bob = userManager.findUser("bob");
        User carol = userManager.findUser("carol");
        
        // Bob deposits and sends to Carol
        bob.subtractCash(new BigDecimal("3000.00"));
        bob.getSavingsAccount().deposit(new BigDecimal("3000.00"));
        bob.getSavingsAccount().withdraw(new BigDecimal("1000.00"));
        carol.addCash(new BigDecimal("1000.00"));
        
        assertEquals(new BigDecimal("2000.00"), bob.getSavingsAccount().getBalance());
        assertEquals(new BigDecimal("11000.00"), carol.getCash());
    }
    
    @Test
    void testFraudDetectionWorkflow() {
        userManager.registerUser("Dave", "Miller", "dave", "password");
        User dave = userManager.findUser("dave");
        
        // Make 3 large suspicious transactions (over $10k)
        for (int i = 0; i < 3; i++) {
            Transaction tx = new Transaction(
                LocalDateTime.now(),
                Transaction.Type.WITHDRAW,
                new BigDecimal("15000.00"),
                "Test withdrawal",
                BigDecimal.ZERO
            );
            dave.addTransaction(tx);
            assertTrue(fraudDetector.isSuspicious(tx));
        }
        
        // Verify account should be frozen
       assertTrue(fraudDetector.shouldFreezeAccount(dave, LocalDateTime.now()));
    }
    
    @Test
    void testCompleteInvestmentWorkflow() {
        userManager.registerUser("Eve", "Wilson", "eve", "secure");
        User eve = userManager.findUser("eve");
        
        // Invest in funds
        eve.subtractCash(new BigDecimal("3000.00"));
        eve.getInvestmentAccount().deposit(new BigDecimal("3000.00"));
        eve.getInvestmentAccount().investInFund(model.Fund.LOW_RISK, new BigDecimal("1000.00"));
        eve.getInvestmentAccount().investInFund(model.Fund.MEDIUM_RISK, new BigDecimal("1000.00"));
        eve.getInvestmentAccount().investInFund(model.Fund.HIGH_RISK, new BigDecimal("1000.00"));
        
        assertEquals(new BigDecimal("1000.00"), eve.getInvestmentAccount().getFundBalance(model.Fund.LOW_RISK));
        
        // Apply gains
        timeManager.applyDailyGains(eve);
        
        assertEquals(new BigDecimal("1020.00"), eve.getInvestmentAccount().getFundBalance(model.Fund.LOW_RISK));
        assertEquals(new BigDecimal("1050.00"), eve.getInvestmentAccount().getFundBalance(model.Fund.MEDIUM_RISK));
        assertEquals(new BigDecimal("1100.00"), eve.getInvestmentAccount().getFundBalance(model.Fund.HIGH_RISK));
    }
    
    @Test
    void testMultiDayInterestAccumulation() {
        userManager.registerUser("Frank", "Taylor", "frank", "pwd");
        userManager.registerUser("Grace", "Brown", "grace", "pwd");
        
        User frank = userManager.findUser("frank");
        User grace = userManager.findUser("grace");
        
        frank.subtractCash(new BigDecimal("1000.00"));
        frank.getSavingsAccount().deposit(new BigDecimal("1000.00"));
        
        grace.subtractCash(new BigDecimal("1000.00"));
        grace.getInvestmentAccount().deposit(new BigDecimal("1000.00"));
        grace.getInvestmentAccount().investInFund(model.Fund.MEDIUM_RISK, new BigDecimal("1000.00"));
        
        // Advance 3 days
        timeManager.advanceTime(3);
        
        assertEquals(new BigDecimal("1030.30"), frank.getSavingsAccount().getBalance());
        assertEquals(new BigDecimal("1157.63"), grace.getInvestmentAccount().getFundBalance(model.Fund.MEDIUM_RISK));
    }
    
    @Test
    void testCompleteDataPersistenceWorkflow() {
        userManager.registerUser("Henry", "Davis", "henry", "secret");
        User henry = userManager.findUser("henry");
        
        // Set up state
        henry.subtractCash(new BigDecimal("3000.00"));
        henry.getSavingsAccount().deposit(new BigDecimal("2000.00"));
        henry.getInvestmentAccount().deposit(new BigDecimal("1000.00"));
        henry.getInvestmentAccount().investInFund(model.Fund.HIGH_RISK, new BigDecimal("500.00"));
        
        // Simulate persistence
        var savedUsers = new java.util.HashMap<>(userManager.getAllUsers());
        userManager.getAllUsers().clear();
        for (var entry : savedUsers.entrySet()) {
            userManager.getAllUsers().put(entry.getKey(), entry.getValue());
        }
        
        // Verify
        User loadedHenry = userManager.findUser("henry");
        assertNotNull(loadedHenry);
        assertEquals(new BigDecimal("7000.00"), loadedHenry.getCash());
        assertEquals(new BigDecimal("2000.00"), loadedHenry.getSavingsAccount().getBalance());
        assertEquals(new BigDecimal("500.00"), loadedHenry.getInvestmentAccount().getFundBalance(model.Fund.HIGH_RISK));
    }
}
