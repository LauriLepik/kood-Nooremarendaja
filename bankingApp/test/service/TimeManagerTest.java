package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import model.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TimeManagerTest {
    
    private TimeManager timeManager;
    private UserManager userManager;
    
    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        timeManager = new TimeManager(userManager);
    }
    
    @Test
    void testAdvanceTimeIncrementsDays() {
        LocalDateTime before = timeManager.getCurrentTime();
        timeManager.advanceTime(5);
        LocalDateTime after = timeManager.getCurrentTime();
        
        assertEquals(5, java.time.temporal.ChronoUnit.DAYS.between(before, after));
    }
    
    @Test
    void testGetCurrentTimeReturnsLocalDateTime() {
        LocalDateTime current = timeManager.getCurrentTime();
        
        assertNotNull(current);
        assertTrue(current instanceof LocalDateTime);
    }
    
    @Test
    void testGetCurrentFormattedTime() {
        String formatted = timeManager.getCurrentFormattedTime();
        
        assertNotNull(formatted);
        // Should match pattern: yyyy-MM-dd HH:mm
        assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"));
    }
    
    @Test
    void testCalculateDaysPassed() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 6, 12, 0);
        
        long days = timeManager.calculateDaysPassed(start, end);
        
        assertEquals(5, days);
    }
    
    @Test
    void testCalculateDaysPassedWithNullReturnsZero() {
        assertEquals(0, timeManager.calculateDaysPassed(null, LocalDateTime.now()));
        assertEquals(0, timeManager.calculateDaysPassed(LocalDateTime.now(), null));
        assertEquals(0, timeManager.calculateDaysPassed(null, null));
    }
    
    @Test
    void testApplyDailyGainsToBothAccounts() {
        User user = new User("Test", "User", "testuser", "password");
        user.getSavingsAccount().deposit(new java.math.BigDecimal("1000.00"));
        user.getInvestmentAccount().deposit(new java.math.BigDecimal("1000.00"));
        
        timeManager.applyDailyGains(user);
        
        // Savings: 1000 * 1.01 = 1010
        assertEquals(new java.math.BigDecimal("1010.00"), user.getSavingsAccount().getBalance());
        // Investment with no funds invested stays same
        assertEquals(new java.math.BigDecimal("1000.00"), user.getInvestmentAccount().getBalance());
    }
}
