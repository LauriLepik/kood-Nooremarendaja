package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {
    
    @Test
    void testEncryptBasicString() {
        String plaintext = "hello";
        String encrypted = SecurityUtils.encrypt(plaintext);
        
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
    }
    
    @Test
    void testDecryptReversesEncrypt() {
        String original = "password123";
        String encrypted = SecurityUtils.encrypt(original);
        String decrypted = SecurityUtils.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
    
    @Test
    void testEncryptDecryptPreservesUppercase() {
        String original = "UPPERCASE";
        String encrypted = SecurityUtils.encrypt(original);
        String decrypted = SecurityUtils.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
    
    @Test
    void testEncryptDecryptPreservesLowercase() {
        String original = "lowercase";
        String encrypted = SecurityUtils.encrypt(original);
        String decrypted = SecurityUtils.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
    
    @Test
    void testEncryptDecryptWithMixedCase() {
        String original = "MiXeDCaSe";
        String encrypted = SecurityUtils.encrypt(original);
        String decrypted = SecurityUtils.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
    
    @Test
    void testEncryptDecryptWithNonAlphabeticCharacters() {
        String original = "pass123!@#";
        String encrypted = SecurityUtils.encrypt(original);
        String decrypted = SecurityUtils.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
    
    @Test
    void testEncryptDecryptWithSpaces() {
        String original = "hello world";
        String encrypted = SecurityUtils.encrypt(original);
        String decrypted = SecurityUtils.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
    
    @Test
    void testEncryptDecryptRoundTripWithComplexPassword() {
        String original = "MyP@ssw0rd!2023";
        String encrypted = SecurityUtils.encrypt(original);
        String decrypted = SecurityUtils.decrypt(encrypted);
        
        assertEquals(original, decrypted);
    }
    
    @Test
    void testEncryptProducesDifferentOutputThanInput() {
        String plaintext = "secrets";
        String encrypted = SecurityUtils.encrypt(plaintext);
        
        assertNotEquals(plaintext, encrypted);
    }
    
    @Test
    void testEncryptConsistentForSameInput() {
        String plaintext = "consistent";
        String encrypted1 = SecurityUtils.encrypt(plaintext);
        String encrypted2 = SecurityUtils.encrypt(plaintext);
        
        assertEquals(encrypted1, encrypted2);
    }
}
