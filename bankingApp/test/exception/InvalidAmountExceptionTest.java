package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidAmountExceptionTest {
    
    @Test
    void testExceptionMessage() {
        String message = "Amount must be positive";
        InvalidAmountException exception = new InvalidAmountException(message);
        
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testExceptionInheritance() {
        InvalidAmountException exception = new InvalidAmountException("test");
        
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testExceptionCanBeThrown() {
        assertThrows(InvalidAmountException.class, () -> {
            throw new InvalidAmountException("Invalid amount");
        });
    }
}
