package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FundTest {
    
    @Test
    void testLowRiskFundRate() {
        assertEquals(0.02, Fund.LOW_RISK.getRate(), 0.001);
    }
    
    @Test
    void testMediumRiskFundRate() {
        assertEquals(0.05, Fund.MEDIUM_RISK.getRate(), 0.001);
    }
    
    @Test
    void testHighRiskFundRate() {
        assertEquals(0.10, Fund.HIGH_RISK.getRate(), 0.001);
    }
    
    @Test
    void testAllFundValuesExist() {
        Fund[] funds = Fund.values();
        assertEquals(3, funds.length);
    }
    
    @Test
    void testFundEnumValues() {
        assertNotNull(Fund.valueOf("LOW_RISK"));
        assertNotNull(Fund.valueOf("MEDIUM_RISK"));
        assertNotNull(Fund.valueOf("HIGH_RISK"));
    }
}
