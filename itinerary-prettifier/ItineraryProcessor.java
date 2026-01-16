
import extended.AirportLookup; // Moved to extended from root for functionality
import java.time.*;
import java.time.format.*;
import java.util.regex.*;

// Handles all the text transformations for itinerary data.
public class ItineraryProcessor {
    
    private final AirportLookup lookup;
    
    // City code patterns (with * prefix)
    private static final Pattern CITY_ICAO_PATTERN = Pattern.compile("\\*##([A-Z]{4})");
    private static final Pattern CITY_IATA_PATTERN = Pattern.compile("\\*#([A-Z]{3})");
    
    // Airport code patterns
    private static final Pattern ICAO_PATTERN = Pattern.compile("##([A-Z]{4})");
    private static final Pattern IATA_PATTERN = Pattern.compile("#([A-Z]{3})");
    
    // Date/time patterns from the input format
    private static final Pattern DATE_PATTERN = Pattern.compile("D\\(([^)]+)\\)");
    private static final Pattern TIME12_PATTERN = Pattern.compile("T12\\(([^)]+)\\)");
    private static final Pattern TIME24_PATTERN = Pattern.compile("T24\\(([^)]+)\\)");
    
    // Standard output format for dates
    private static final DateTimeFormatter OUTPUT_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.ENGLISH);
    
    public ItineraryProcessor(AirportLookup lookup) {
        this.lookup = lookup;
    }
    
    // Main processing - runs everything in order.
    public String process(String input) {
        String result = input;
        
        // City codes first so they don't get confused with airport codes
        result = replaceCityCodes(result);
        result = replaceIcaoCodes(result);
        result = replaceIataCodes(result);
        result = formatDates(result);
        result = formatTimes(result);
        result = cleanupWhitespace(result);
        
        return result;
    }
    
    private String formatDates(String input) {
        Matcher matcher = DATE_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();
        
        while (matcher.find()) {
            String isoDate = matcher.group(1);
            try {
                OffsetDateTime dt = OffsetDateTime.parse(isoDate);
                String formatted = dt.format(OUTPUT_DATE);
                matcher.appendReplacement(result, Matcher.quoteReplacement(formatted));
            } catch (DateTimeParseException e) {
                // Couldn't parse it, just leave it as-is
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
    
    private String replaceCityCodes(String input) {
        String result = input;
        
        // Process ICAO city codes (*##XXXX)
        Matcher matcher = CITY_ICAO_PATTERN.matcher(result);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String code = matcher.group(1);
            String city = lookup.getCityName(code);
            if (city != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(city));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        result = sb.toString();
        
        // Process IATA city codes (*#XXX)
        matcher = CITY_IATA_PATTERN.matcher(result);
        sb = new StringBuilder();
        while (matcher.find()) {
            String code = matcher.group(1);
            String city = lookup.getCityName(code);
            if (city != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(city));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    private String replaceIcaoCodes(String input) {
        Matcher matcher = ICAO_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();
        
        while (matcher.find()) {
            String code = matcher.group(1);
            String name = lookup.getAirportName(code);
            if (name != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(name));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
    
    private String replaceIataCodes(String input) {
        Matcher matcher = IATA_PATTERN.matcher(input);
        StringBuilder result = new StringBuilder();
        
        while (matcher.find()) {
            String code = matcher.group(1);
            String name = lookup.getAirportName(code);
            if (name != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(name));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
    
    private String formatTimes(String input) {
        String result = input;
        
        // Process 12-hour time format tags
        Matcher matcher = TIME12_PATTERN.matcher(result);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String isoTime = matcher.group(1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(formatTime12(isoTime)));
        }
        matcher.appendTail(sb);
        result = sb.toString();
        
        // Process 24-hour time format tags
        matcher = TIME24_PATTERN.matcher(result);
        sb = new StringBuilder();
        while (matcher.find()) {
            String isoTime = matcher.group(1);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(formatTime24(isoTime)));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }
    
    private String formatTime12(String isoTime) {
        try {
            OffsetDateTime dt = OffsetDateTime.parse(isoTime);
            int hour = dt.getHour();
            int minute = dt.getMinute();
            String ampm = hour >= 12 ? "PM" : "AM";
            int hour12 = hour % 12;
            if (hour12 == 0) hour12 = 12;
            String offset = formatOffset(dt.getOffset());
            return String.format("%02d:%02d%s (%s)", hour12, minute, ampm, offset);
        } catch (DateTimeParseException e) {
            return "T12(" + isoTime + ")";
        }
    }
    
    private String formatTime24(String isoTime) {
        try {
            OffsetDateTime dt = OffsetDateTime.parse(isoTime);
            int hour = dt.getHour();
            int minute = dt.getMinute();
            String offset = formatOffset(dt.getOffset());
            return String.format("%02d:%02d (%s)", hour, minute, offset);
        } catch (DateTimeParseException e) {
            return "T24(" + isoTime + ")";
        }
    }
    
    private String formatOffset(ZoneOffset offset) {
        // Normalize UTC representation
        if (offset.getTotalSeconds() == 0) {
            return "+00:00";
        }
        return offset.toString();
    }
    
    private String cleanupWhitespace(String input) {
        // Handle different line ending styles
        String result = input.replace("\r\n", "\n")
                             .replace("\r", "\n")
                             .replace("\f", "\n")
                             .replace("\u000B", "\n");
        
        // Don't let there be more than one blank line in a row
        while (result.contains("\n\n\n")) {
            result = result.replace("\n\n\n", "\n\n");
        }
        
        return result;
    }
}
