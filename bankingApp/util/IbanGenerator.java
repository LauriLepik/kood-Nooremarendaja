package util;

import java.math.BigInteger;
import java.util.Random;

public class IbanGenerator {
    private static final String COUNTRY_CODE = "EE"; // Estonia
    private static final String BANK_CODE = "77";    // Green Day Bank internal code
    private static final Random random = new Random();

    // Generates a random valid Estonian-style IBAN for the showcase
    // Format: EEkk 77xx xxxx (12 characters total)
    // 'kk' is checksum, '77' is bank code, 'xxxxxx' is account number
    public static String generateIban() {
        // 1. Generate 6 random digits for account part
        StringBuilder accountPart = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            accountPart.append(random.nextInt(10));
        }

        // 2. Build string for checksum calculation: BankCode + Account + CountryCode(Numbers) + "00"
        // E = 14 (A=10, B=11 ... E=14)
        // EE = 1414
        String temp = BANK_CODE + accountPart.toString() + "141400";
        
        // 3. Calculate Checksum: 98 - (temp % 97)
        BigInteger bigInt = new BigInteger(temp);
        int remainder = bigInt.mod(BigInteger.valueOf(97)).intValue();
        int checkDigits = 98 - remainder;
        
        // 4. Format check digits (pad with 0 if < 10)
        String checkString = (checkDigits < 10 ? "0" : "") + checkDigits;

        // 5. Assemble final IBAN
        return COUNTRY_CODE + checkString + BANK_CODE + accountPart.toString();
    }

    public static boolean validateIban(String iban) {
        if (iban == null || iban.length() != 12 || !iban.startsWith(COUNTRY_CODE)) {
            return false;
        }
        // Basic length check for this showcase
        return true;
    }
}
