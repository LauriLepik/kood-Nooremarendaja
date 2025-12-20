package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import model.User;
import exception.InvalidAmountException;
import exception.InsufficientFundsException;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {
    
    private UserManager userManager;
    private TimeManager timeManager;
    private FraudDetector fraudDetector;
    private InputValidator inputValidator;
    private TransferService transferService;
    
    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        timeManager = new TimeManager(userManager);
        fraudDetector = new FraudDetector();
    }
    
    @Test
    void testTransferBetweenAccountsSavingsToInvestment() throws InvalidAmountException, InsufficientFundsException {
        User user = new User("Test", "User", "testuser", "password");
        user.getSavingsAccount().deposit(new BigDecimal("1000.00"));
        
        String input = "1\n500.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        inputValidator = new InputValidator(scanner);
        transferService = new TransferService(userManager, timeManager, fraudDetector, inputValidator, scanner);
        
        transferService.transferBetweenAccounts(user);
        
        assertEquals(new BigDecimal("500.00"), user.getSavingsAccount().getBalance());
        assertEquals(new BigDecimal("500.00"), user.getInvestmentAccount().getBalance());
    }
    
    @Test
    void testTransferBetweenAccountsInvestmentToSavings() throws InvalidAmountException, InsufficientFundsException {
        User user = new User("Test", "User", "testuser", "password");
        user.getInvestmentAccount().deposit(new BigDecimal("800.00"));
        
        String input = "2\n300.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        inputValidator = new InputValidator(scanner);
        transferService = new TransferService(userManager, timeManager, fraudDetector, inputValidator, scanner);
        
        transferService.transferBetweenAccounts(user);
        
        assertEquals(new BigDecimal("300.00"), user.getSavingsAccount().getBalance());
        assertEquals(new BigDecimal("500.00"), user.getInvestmentAccount().getBalance());
    }
    
    @Test
    void testTransferBetweenAccountsWithInsufficientFunds() {
        User user = new User("Test", "User", "testuser", "password");
        user.getSavingsAccount().deposit(new BigDecimal("100.00"));
        
        // Try to transfer more than available
        String input = "1\n500.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        inputValidator = new InputValidator(scanner);
        transferService = new TransferService(userManager, timeManager, fraudDetector, inputValidator, scanner);
        
        transferService.transferBetweenAccounts(user);
        
        // Should remain unchanged
        assertEquals(new BigDecimal("100.00"), user.getSavingsAccount().getBalance());
        assertEquals(BigDecimal.ZERO, user.getInvestmentAccount().getBalance());
    }
    
    @Test
    void testTransferBetweenAccountsWithInvalidChoice() {
        User user = new User("Test", "User", "testuser", "password");
        
        String input = "3\n";  // Invalid choice
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        inputValidator = new InputValidator(scanner);
        transferService = new TransferService(userManager, timeManager, fraudDetector, inputValidator, scanner);
        
        transferService.transferBetweenAccounts(user);
        
        // Balances should remain unchanged
        assertEquals(BigDecimal.ZERO, user.getSavingsAccount().getBalance());
        assertEquals(BigDecimal.ZERO, user.getInvestmentAccount().getBalance());
    }
    
    @Test
    void testSendMoneyQuickTransferWithNoPreviousRecipients() {
        User sender = new User("Alice", "Smith", "alice", "password");
        userManager.registerUser("Alice", "Smith", "alice", "password");
        
        // Choose quick transfer (1), but no previous recipients
        String input = "1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        inputValidator = new InputValidator(scanner);
        transferService = new TransferService(userManager, timeManager, fraudDetector, inputValidator, scanner);
        
        boolean result = transferService.sendMoney(sender);
        
        assertTrue(result);  // Returns true (continues)
        assertEquals(new BigDecimal("10000.00"), sender.getCash());  // Cash unchanged
    }
}
