import java.util.Scanner;

public class CypherTool {
    
    public static void main(String[] args) {
        System.out.println("Welcome to the Cypher Tool!");
        
        Scanner scanner = new Scanner(System.in);

        while (true) {
            InputData input = getInput(scanner);
            if (input == null) {
                System.out.println("Exiting Cypher Tool. Goodbye!");
                break;
            }

            int op = input.getOperation();
            int cypher = input.getCypher();
            String message = input.getMessage();

            //Perform Operation
            if (op == 1) { //Encrypt
                switch (cypher) {
                    case 1 -> { //Rot13
                        String rot13Output = encryptRot13(message);
                        while (rot13Output == null) {
                            System.out.println("Invalid message. Please insert a valid message:");
                            message = scanner.nextLine().trim();
                             if (message.equalsIgnoreCase("exit")) {
                                System.out.println("Exiting Cypher Tool. Goodbye!");
                                return;
                            }
                            rot13Output = encryptRot13(message);
                        }
                        System.out.println("Encrypted message: " + rot13Output);
                    }
                    case 2 -> { //Atbash
                        String atbashOutput = encryptAtbash(message);
                        System.out.println("Encrypted message: " + atbashOutput);
                    }
                    case 3 -> { //Affine
                        String output = encryptAffine(message);
                        System.out.println("Encrypted message: " + output);
                    }
                }
            } else if (op == 2) { //Decrypt
                switch (cypher) {
                    case 1 -> { //Rot13
                        String rot13Output = decryptRot13(message);
                        System.out.println("Decrypted message: " + rot13Output);
                    }
                    case 2 -> { //Atbash
                        String atbashOutput = decryptAtbash(message);
                        System.out.println("Decrypted message: " + atbashOutput);
                    }
                    case 3 -> { //Affine
                        String output = decryptAffine(message);
                        System.out.println("Decrypted message: " + output);
                    }
                }
            }
        }
        
    }

    public static InputData getInput(Scanner scanner) {
        int op = 0;
        int cypher = 0;

        //Operation Selection
        while (true) {
            System.out.println("Please select an operation (or type 'exit' to quit):");
            System.out.println("1. Encrypt");
            System.out.println("2. Decrypt");
            try {
                String line = scanner.nextLine().trim();
                if (line.equalsIgnoreCase("exit")) {
                    return null;
                }
                op = Integer.parseInt(line);
                if (op == 1 || op == 2) {
                    break;
                }
                System.out.println("Invalid selection. Please enter 1 or 2.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        //Cypher Selection
        while (true) {
            System.out.println("Please select a cypher (or type 'exit' to quit):");
            System.out.println("1. Rot13");
            System.out.println("2. Atbash");
            System.out.println("3. Affine");
            try {
                String line = scanner.nextLine().trim();
                 if (line.equalsIgnoreCase("exit")) {
                    return null;
                }
                cypher = Integer.parseInt(line);
                if (cypher >= 1 && cypher <= 3) {
                    break;
                }
                System.out.println("Invalid selection. Please enter 1, 2, or 3.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        //Enter Message
        String message = "";
        while (true) {
            System.out.println("Enter the message:");
            message = scanner.nextLine().trim();
            if (!message.isEmpty()) {
                break;
            }
            System.out.println("Message cannot be empty. Please try again.");
        }

        return new InputData(op, cypher, message);
    }

    public static String encryptRot13(String s) {
        if (s == null) return null;
        
        StringBuilder out = new StringBuilder(s.length());
        StringBuilder ignored = new StringBuilder(); // store non-ROT13 characters

        for (char c : s.toCharArray()) {

            // Lowercase letters
            if (c >= 'a' && c <= 'z') {
                out.append((char) ((c - 'a' + 13) % 26 + 'a'));

            // Uppercase letters
            } else if (c >= 'A' && c <= 'Z') {
                out.append((char) ((c - 'A' + 13) % 26 + 'A'));

            // Not a valid ROT13 character → keep unchanged, but record it
            } else {
                out.append(c);
                ignored.append(c).append(' ');
            }
        }
        // If any invalid characters were found, notify the user
        if (ignored.length() > 0) {
            System.out.println("Note: The following characters were not encrypted (not part of ROT13):");
            System.out.println(ignored.toString().trim());
        }

        return out.toString();
    }

    public static String encryptAtbash(String s) {
        if (s == null) return null;

        StringBuilder encrypted = new StringBuilder(); //ehitab uue lause

        for (char c : s.toCharArray()) { // funktsioon, mis teisendab stringi tähemärkide jadaks
            if (c >= 'A' && c <= 'Z') { // suured t2hem2rgid
                int newChar = 'A' + ('Z' - c);
                encrypted.append((char) newChar);
            }
            else if (c >= 'a' && c <= 'z') {
                int newChar = 'a' + ('z' - c); //v2ikesed t2hem2rgid
                encrypted.append((char) newChar); //lisab uued symboli kokku
            }
            else {
                 encrypted.append(c); 
            }
            
        }
        return encrypted.toString();
    }

    public static String encryptAffine(String s) {
        if (s == null) return null;
        StringBuilder result = new StringBuilder();
        // Key: a=5, b=8  -> E(x) = (5x + 8) % 26
        
        for (char c : s.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                int x = c - 'a';
                int encryptedFn = (5 * x + 8) % 26;
                result.append((char) (encryptedFn + 'a'));
            } else if (c >= 'A' && c <= 'Z') {
                int x = c - 'A';
                int encryptedFn = (5 * x + 8) % 26;
                result.append((char) (encryptedFn + 'A'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    //ROT13 is symmetric, so encryption = decryption
    public static String decryptRot13(String s) {
        return encryptRot13(s);
    }

    public static String decryptAtbash(String s) {

        return encryptAtbash(s); //kuna valem on sama, siis l2heb tagasi eelmise koodi juurde
    }

    public static String decryptAffine(String s) {
        if (s == null) return null;
        StringBuilder result = new StringBuilder();
        // Key: a=5, b=8 -> D(x) = a^-1 * (x - b) % 26
        // a^-1 for 5 mod 26 is 21.
        
        for (char c : s.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                int x = c - 'a';
                // Java % definition can return negative, so add 26
                int decryptedFn = (21 * (x - 8)) % 26;
                if (decryptedFn < 0) decryptedFn += 26;
                result.append((char) (decryptedFn + 'a'));
            } else if (c >= 'A' && c <= 'Z') {
                int x = c - 'A';
                int decryptedFn = (21 * (x - 8)) % 26;
                 if (decryptedFn < 0) decryptedFn += 26;
                result.append((char) (decryptedFn + 'A'));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}