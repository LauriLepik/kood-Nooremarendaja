package util;

public class SecurityUtils {
    // Affine Cipher keys
    // E(x) = (ax + b) mod m
    // D(x) = a^-1 (x - b) mod m
    // m = 26 (Standard English Alphabet)
    // a = 5 (Coprime to 26)
    // b = 8
    
    private static final int A = 5;
    private static final int B = 8;
    private static final int M = 26;
    private static final int A_INVERSE = 21;

    public static String encrypt(String input) {
        StringBuilder result = new StringBuilder();
        for (char character : input.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                int x = character - base;
                // E(x) = (ax + b) mod m
                int encryptedChar = (A * x + B) % M;
                result.append((char) (base + encryptedChar));
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public static String decrypt(String input) {
        StringBuilder result = new StringBuilder();
        for (char character : input.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                int y = character - base;
                // D(x) = a^-1 (y - b) mod m
                // Adjust for negative result
                int decryptedChar = (A_INVERSE * (y - B)) % M;
                if (decryptedChar < 0) {
                    decryptedChar += M;
                }
                result.append((char) (base + decryptedChar));
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }
}
