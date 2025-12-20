package util;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class IbanGeneratorTest {
    
    @Test
    void testGenerateIbanProduces12CharacterString() {
        String iban = IbanGenerator.generateIban();
        
        assertEquals(12, iban.length());
    }
    
    @Test
    void testGenerateIbanStartsWithEE() {
        String iban = IbanGenerator.generateIban();
        
        assertTrue(iban.startsWith("EE"));
    }
    
    @Test
    void testGenerateIbanContainsBankCode77() {
        String iban = IbanGenerator.generateIban();
        
        // Format: EEkk77xxxxxx where kk is checksum, 77 is bank code
        assertTrue(iban.substring(4, 6).equals("77"));
    }
    
    @Test
    void testGenerateIbanProducesUniqueValues() {
        Set<String> ibans = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            ibans.add(IbanGenerator.generateIban());
        }
        
        // Should generate at least 95 unique IBANs out of 100
        // (allowing small chance of collision)
        assertTrue(ibans.size() >= 95);
    }
    
    @Test
    void testValidateIbanWithValidIban() {
        String validIban = IbanGenerator.generateIban();
        
        assertTrue(IbanGenerator.validateIban(validIban));
    }
    
    @Test
    void testValidateIbanWithNullReturnsFalse() {
        assertFalse(IbanGenerator.validateIban(null));
    }
    
    @Test
    void testValidateIbanWithWrongLengthReturnsFalse() {
        assertFalse(IbanGenerator.validateIban("EE12345"));
        assertFalse(IbanGenerator.validateIban("EE1234567890123"));
    }
    
    @Test
    void testValidateIbanWithWrongCountryCodeReturnsFalse() {
        assertFalse(IbanGenerator.validateIban("LT1234567890"));
        assertFalse(IbanGenerator.validateIban("FI1234567890"));
    }
    
    @Test
    void testGeneratedIbansAreValid() {
        for (int i = 0; i < 20; i++) {
            String iban = IbanGenerator.generateIban();
            assertTrue(IbanGenerator.validateIban(iban), 
                "Generated IBAN should be valid: " + iban);
        }
    }
}
