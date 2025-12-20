package util;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleUtilsTest {
    
    @Test
    void testFormatSuccessAddsGreenColorCode() {
        String result = ConsoleUtils.formatSuccess("Operation successful");
        
        assertTrue(result.contains("[OK]"));
        assertTrue(result.contains("Operation successful"));
        assertTrue(result.contains(ConsoleUtils.GREEN));
        assertTrue(result.contains(ConsoleUtils.RESET));
    }
    
    @Test
    void testFormatErrorAddsRedColorCode() {
        String result = ConsoleUtils.formatError("Error occurred");
        
        assertTrue(result.contains("[ERROR]"));
        assertTrue(result.contains("Error occurred"));
        assertTrue(result.contains(ConsoleUtils.RED));
        assertTrue(result.contains(ConsoleUtils.RESET));
    }
    
    @Test
    void testFormatMoneyWithPositiveAmount() {
        String result = ConsoleUtils.formatMoney(new BigDecimal("123.45"));
        
        assertTrue(result.contains("$123.45"));
        assertTrue(result.contains(ConsoleUtils.GREEN));
        assertTrue(result.contains(ConsoleUtils.RESET));
    }
    
    @Test
    void testFormatMoneyWithNegativeAmount() {
        String result = ConsoleUtils.formatMoney(new BigDecimal("-50.00"));
        
        assertTrue(result.contains("-$50.00"));
        assertTrue(result.contains(ConsoleUtils.RED));
        assertTrue(result.contains(ConsoleUtils.RESET));
    }
    
    @Test
    void testFormatMoneyWithZero() {
        String result = ConsoleUtils.formatMoney(BigDecimal.ZERO);
        
        assertTrue(result.contains("$0"));
        assertTrue(result.contains(ConsoleUtils.GREEN));
    }
    
    @Test
    void testPrintWelcomeBannerDoesNotThrow() {
        // Just verify it doesn't crash
        assertDoesNotThrow(() -> {
            ConsoleUtils.printWelcomeBanner();
        });
    }
    
    @Test
    void testPrintAccountFrozenBannerDoesNotThrow() {
        // Just verify it doesn't crash
        assertDoesNotThrow(() -> {
            ConsoleUtils.printAccountFrozenBanner();
        });
    }
}
