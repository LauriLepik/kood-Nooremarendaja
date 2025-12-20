package service;

import java.math.BigDecimal;
import java.util.Scanner;
import exception.InvalidAmountException;

public class InputValidator {
    private final Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    public BigDecimal readAmount() throws InvalidAmountException {
        if (!scanner.hasNextLine()) {
            return null;
        }
        String input = scanner.nextLine().trim();
        
        try {
            BigDecimal amount = new BigDecimal(input);
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidAmountException("amount must be positive");
            }
            
            return amount;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
