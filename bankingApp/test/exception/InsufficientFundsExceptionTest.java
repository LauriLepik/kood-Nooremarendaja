package exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InsufficientFundsExceptionTest {
    
    @Test
    void testExceptionMessage() {
        String message = "Not enough funds";
        InsufficientFundsException exception = new InsufficientFundsException(message);
        
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testExceptionInheritance() {
        InsufficientFundsException exception = new InsufficientFundsException("test");
        
        assertTrue(exception instanceof Exception);
    }
    
    @Test
    void testExceptionCanBeThrown() {
        assertThrows(InsufficientFundsException.class, () -> {
            throw new InsufficientFundsException("Insufficient funds");
        });
    }
}
