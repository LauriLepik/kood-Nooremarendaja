package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {
    
    private SavingsAccount account;
    
    @BeforeEach
    void setUp() {
        account = new SavingsAccount();
    }
    
    @Test
    void testInitialBalanceIsZero() {
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }
    
    @Test
    void testDeposit() {
        BigDecimal amount = new BigDecimal("100.00");
        account.deposit(amount);
        
        assertEquals(amount, account.getBalance());
    }
    
    @Test
    void testMultipleDeposits() {
        account.deposit(new BigDecimal("50.00"));
        account.deposit(new BigDecimal("30.00"));
        
        assertEquals(new BigDecimal("80.00"), account.getBalance());
    }
    
    @Test
    void testWithdrawWithSufficientFunds() {
        account.deposit(new BigDecimal("100.00"));
        account.withdraw(new BigDecimal("40.00"));
        
        assertEquals(new BigDecimal("60.00"), account.getBalance());
    }
    
    @Test
    void testWithdrawWithInsufficientFundsThrowsException() {
        account.deposit(new BigDecimal("50.00"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            account.withdraw(new BigDecimal("100.00"));
        });
    }
    
    @Test
    void testApplyGainsWithOnePercentInterest() {
        account.deposit(new BigDecimal("1000.00"));
        account.applyGains();
        
        // 1000 * 0.01 = 10, so balance should be 1010
        assertEquals(new BigDecimal("1010.00"), account.getBalance());
    }
    
    @Test
    void testApplyGainsOnZeroBalance() {
        account.applyGains();
        
        assertEquals(new BigDecimal("0.00"), account.getBalance());
    }
}
