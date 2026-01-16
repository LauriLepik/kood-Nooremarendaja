package extended;

// Turns country codes like "US" into flag emojis like 🇺🇸.
public class CountryFlags {
    
    // Emoji math - each letter maps to a regional indicator.
    public static String getFlag(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return "";
        }
        
        // Unicode magic: A=🇦, B=🇧, etc.
        int firstChar = Character.codePointAt(countryCode.toUpperCase(), 0) - 'A' + 0x1F1E6;
        int secondChar = Character.codePointAt(countryCode.toUpperCase(), 1) - 'A' + 0x1F1E6;
        
        return new String(Character.toChars(firstChar)) + new String(Character.toChars(secondChar));
    }
}
