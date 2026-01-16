package lib;

import java.util.HashMap;
import java.util.Map;

// Maps UTC offsets to common timezone abbreviations.
public class TimezoneAbbreviations {
    
    private static final Map<String, String> OFFSET_TO_ABBREV = new HashMap<>();
    
    static {
        // UTC/GMT
        OFFSET_TO_ABBREV.put("Z", "UTC");
        OFFSET_TO_ABBREV.put("+00:00", "UTC");
        OFFSET_TO_ABBREV.put("-00:00", "UTC");
        
        // Europe
        OFFSET_TO_ABBREV.put("+01:00", "CET");   // Central European
        OFFSET_TO_ABBREV.put("+02:00", "EET");   // Eastern European
        OFFSET_TO_ABBREV.put("+03:00", "MSK");   // Moscow
        
        // Americas
        OFFSET_TO_ABBREV.put("-05:00", "EST");   // Eastern Standard
        OFFSET_TO_ABBREV.put("-04:00", "EDT");   // Eastern Daylight / Atlantic
        OFFSET_TO_ABBREV.put("-06:00", "CST");   // Central Standard
        OFFSET_TO_ABBREV.put("-07:00", "MST");   // Mountain Standard
        OFFSET_TO_ABBREV.put("-08:00", "PST");   // Pacific Standard
        OFFSET_TO_ABBREV.put("-03:00", "BRT");   // Brasilia
        OFFSET_TO_ABBREV.put("-02:00", "GST");   // South Georgia
        
        // Asia
        OFFSET_TO_ABBREV.put("+05:30", "IST");   // India
        OFFSET_TO_ABBREV.put("+08:00", "SGT");   // Singapore/China
        OFFSET_TO_ABBREV.put("+09:00", "JST");   // Japan
        OFFSET_TO_ABBREV.put("+09:30", "ACST");  // Australian Central
        OFFSET_TO_ABBREV.put("+10:00", "AEST");  // Australian Eastern
        OFFSET_TO_ABBREV.put("+07:00", "ICT");   // Indochina
        OFFSET_TO_ABBREV.put("+05:00", "PKT");   // Pakistan
        
        // Middle East
        OFFSET_TO_ABBREV.put("+03:30", "IRST");  // Iran
        OFFSET_TO_ABBREV.put("+04:00", "GST");   // Gulf
        OFFSET_TO_ABBREV.put("+04:30", "AFT");   // Afghanistan
        
        // Pacific
        OFFSET_TO_ABBREV.put("+12:00", "NZST");  // New Zealand
        OFFSET_TO_ABBREV.put("+11:00", "AEDT");  // Australian Eastern Daylight
        OFFSET_TO_ABBREV.put("-10:00", "HST");   // Hawaii
    }
    
    // Get abbreviation for a ZonedDateTime offset
    public static String getAbbreviation(java.time.ZonedDateTime zdt) {
        if (zdt == null) return "";
        
        String offset = zdt.getOffset().getId();
        String abbrev = OFFSET_TO_ABBREV.get(offset);
        
        // If no mapping found, just return the offset
        return abbrev != null ? abbrev : offset;
    }
    
    // Get abbreviation from an offset string
    public static String getAbbreviation(String offset) {
        if (offset == null || offset.isEmpty()) return "";
        String abbrev = OFFSET_TO_ABBREV.get(offset);
        return abbrev != null ? abbrev : offset;
    }
}
