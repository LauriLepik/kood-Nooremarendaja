package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class InvestmentAccountTest {
    
    private InvestmentAccount account;
    
    @BeforeEach
    void setUp() {
        account = new InvestmentAccount();
    }
    
    @Test
    void testInitialBalanceIsZero() {
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }
    
    @Test
    void testDeposit() {
        BigDecimal amount = new BigDecimal("500.00");
        account.deposit(amount);
        
        assertEquals(amount, account.getBalance());
    }
    
    @Test
    void testWithdraw() {
        account.deposit(new BigDecimal("300.00"));
        account.withdraw(new BigDecimal("100.00"));
        
        assertEquals(new BigDecimal("200.00"), account.getBalance());
    }
    
    @Test
    void testInvestInFund() {
        account.deposit(new BigDecimal("1000.00"));
        account.investInFund(Fund.HIGH_RISK, new BigDecimal("300.00"));
        
        assertEquals(new BigDecimal("700.00"), account.getBalance());
        assertEquals(new BigDecimal("300.00"), account.getFundBalance(Fund.HIGH_RISK));
    }
    
    @Test
    void testGetFundBalanceForNonInvestedFund() {
        assertEquals(BigDecimal.ZERO, account.getFundBalance(Fund.LOW_RISK));
    }
    
    @Test
    void testGetTotalFundInvestments() {
        account.deposit(new BigDecimal("1000.00"));
        account.investInFund(Fund.LOW_RISK, new BigDecimal("200.00"));
        account.investInFund(Fund.MEDIUM_RISK, new BigDecimal("300.00"));
        account.investInFund(Fund.HIGH_RISK, new BigDecimal("100.00"));
        
        assertEquals(new BigDecimal("600.00"), account.getTotalFundInvestments());
    }
    
    @Test
    void testWithdrawAllFunds() {
        account.deposit(new BigDecimal("500.00"));
        account.investInFund(Fund.LOW_RISK, new BigDecimal("200.00"));
        account.investInFund(Fund.HIGH_RISK, new BigDecimal("300.00"));
        
        account.withdrawAllFunds();
        
        assertEquals(new BigDecimal("500.00"), account.getBalance());
        assertEquals(BigDecimal.ZERO, account.getTotalFundInvestments());
    }
    
    @Test
    void testApplyGainsForMultipleFunds() {
        account.deposit(new BigDecimal("1000.00"));
        account.investInFund(Fund.LOW_RISK, new BigDecimal("100.00")); // 2% = 2
        account.investInFund(Fund.MEDIUM_RISK, new BigDecimal("100.00")); // 5% = 5
        account.investInFund(Fund.HIGH_RISK, new BigDecimal("100.00")); // 10% = 10
        
        account.applyGains();
        
        assertEquals(new BigDecimal("102.00"), account.getFundBalance(Fund.LOW_RISK));
        assertEquals(new BigDecimal("105.00"), account.getFundBalance(Fund.MEDIUM_RISK));
        assertEquals(new BigDecimal("110.00"), account.getFundBalance(Fund.HIGH_RISK));
    }
    
    @Test
    void testFundsEverInvestedTracking() {
        account.deposit(new BigDecimal("500.00"));
        account.investInFund(Fund.LOW_RISK, new BigDecimal("100.00"));
        account.investInFund(Fund.HIGH_RISK, new BigDecimal("50.00"));
        
        Set<Fund> fundsInvested = account.getFundsEverInvested();
        
        assertEquals(2, fundsInvested.size());
        assertTrue(fundsInvested.contains(Fund.LOW_RISK));
        assertTrue(fundsInvested.contains(Fund.HIGH_RISK));
        assertFalse(fundsInvested.contains(Fund.MEDIUM_RISK));
    }
    
    @Test
    void testApplyGainsOnZeroInvestments() {
        account.applyGains();
        
        assertEquals(BigDecimal.ZERO, account.getTotalFundInvestments());
    }
}
