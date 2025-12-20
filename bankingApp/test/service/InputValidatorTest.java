package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import exception.InvalidAmountException;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {
    
    private InputValidator validator;
    
    @Test
    void testReadAmountWithValidPositiveDecimal() throws InvalidAmountException {
        String input = "123.45\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        validator = new InputValidator(scanner);
        
        BigDecimal result = validator.readAmount();
        
        assertEquals(new BigDecimal("123.45"), result);
    }
    
    @Test
    void testReadAmountWithZeroThrowsException() {
        String input = "0\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        validator = new InputValidator(scanner);
        
        assertThrows(InvalidAmountException.class, () -> {
            validator.readAmount();
        });
    }
    
    @Test
    void testReadAmountWithNegativeThrowsException() {
        String input = "-50.00\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        validator = new InputValidator(scanner);
        
        assertThrows(InvalidAmountException.class, () -> {
            validator.readAmount();
        });
    }
    
    @Test
    void testReadAmountWithInvalidStringReturnsNull() throws InvalidAmountException {
        String input = "notanumber\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        validator = new InputValidator(scanner);
        
        BigDecimal result = validator.readAmount();
        
        assertNull(result);
    }
    
    @Test
    void testReadAmountWithEmptyInputReturnsNull() throws InvalidAmountException {
        String input = "";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        validator = new InputValidator(scanner);
        
        BigDecimal result = validator.readAmount();
        
        assertNull(result);
    }
    
    @Test
    void testReadAmountWithWhitespaceOnly() throws InvalidAmountException {
        String input = "   \n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        validator = new InputValidator(scanner);
        
        BigDecimal result = validator.readAmount();
        
        assertNull(result);
    }
}
