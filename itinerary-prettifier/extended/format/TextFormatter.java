package extended.format;

import extended.Flight;
import extended.ProcessingOptions;
import java.util.List;

public class TextFormatter {
    
    private static final String HEADER = """
            ============================================
                    ANYWHERE HOLIDAYS
                    Your Flight Details
               "Your journey starts here"
            ============================================
            """;
    
    private static final String FOOTER_TEMPLATE = """
            ============================================
                    %s
            
                Booked online through Anywhere Holidays
                   nooremarendaja.kood.tech
            ============================================
            """;
    
    private static final String SEPARATOR_MAJOR = "============================================";
    private static final String SEPARATOR_MINOR = "--------------------------------------------";
    
    public static String getHeader() {
        return HEADER;
    }
    
    public static String getFooter(String wish) {
        return String.format(FOOTER_TEMPLATE, wish);
    }
    
    public static String getSeparatorMajor() {
        return SEPARATOR_MAJOR;
    }
    
    public static String getSeparatorMinor() {
        return SEPARATOR_MINOR;
    }
    
    // Turn flight list into plain text.
    public static String format(List<Flight> flights, String wish, ProcessingOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeader());
        sb.append("\n");
        
        java.time.ZoneId targetZone = null;
        if (options.getTimezone() != null) {
            try {
                targetZone = java.time.ZoneId.of(options.getTimezone());
            } catch (Exception e) { 
                // Bad timezone, ignore it
            }
        }

        String pattern = options.getDateFormat();
        if (pattern == null || pattern.isEmpty()) {
            pattern = "dd MMM yyyy HH:mm";
        }
        
        java.time.format.DateTimeFormatter fmt;
        try {
            fmt = java.time.format.DateTimeFormatter.ofPattern(pattern);
        } catch (Exception e) {
            fmt = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        }
        
        for (extended.Flight flight : flights) {
             sb.append(String.format("Departure: %s\n", flight.getDepartureName()));
             sb.append(String.format("City: %s\n", flight.getDepartureCity()));
             if (flight.getDepartureAddress() != null) {
                 sb.append(String.format("Address: %s\n", flight.getDepartureAddress()));
             }
             sb.append("\n");
             
             sb.append(SEPARATOR_MINOR + "\n\n");
             
             sb.append(String.format("Arrival: %s\n", flight.getArrivalName()));
             sb.append(String.format("City: %s\n", flight.getArrivalCity()));
             if (flight.getArrivalAddress() != null) {
                 sb.append(String.format("Address: %s\n", flight.getArrivalAddress()));
             }
             sb.append("\n");
             
             sb.append(SEPARATOR_MAJOR + "\n\n");
             
             java.time.ZonedDateTime dTime = (targetZone != null && flight.getDepartureTime() != null) 
                    ? flight.getDepartureTime().withZoneSameInstant(targetZone) 
                    : flight.getDepartureTime();
             java.time.ZonedDateTime aTime = (targetZone != null && flight.getArrivalTime() != null) 
                    ? flight.getArrivalTime().withZoneSameInstant(targetZone) 
                    : flight.getArrivalTime();

             if (dTime != null || aTime != null) {
                 if (dTime != null) {
                     String depTz = lib.TimezoneAbbreviations.getAbbreviation(dTime);
                     sb.append(String.format("Departure Time: %s %s\n", dTime.format(fmt), depTz));
                 } else {
                     sb.append("Departure Time: Unknown\n");
                 }

                 if (aTime != null) {
                     String arrTz = lib.TimezoneAbbreviations.getAbbreviation(aTime);
                     sb.append(String.format("Arrival Time:   %s %s\n", aTime.format(fmt), arrTz));
                 }
                 
                 sb.append("\n");
                 
                 if (dTime != null && aTime != null) {
                     // Flight time doesn't change with display timezone
                     java.time.Duration dur = java.time.Duration.between(flight.getDepartureTime(), flight.getArrivalTime());
                     long days = dur.toDays();
                     long hours = dur.toHoursPart();
                     long minutes = dur.toMinutesPart();
                     String durStr = days > 0 
                         ? String.format("%dd %dh %02dm", days, hours, minutes)
                         : String.format("%dh %02dm", hours, minutes);
                     sb.append(String.format("Duration:       %s\n", durStr));
                 }
             }

             sb.append(SEPARATOR_MINOR + "\n\n");
        }
        
        sb.append(getFooter(stripEmojis(wish)));
        return sb.toString();
    }
    
    // Remove emojis for terminal-friendly output
    private static String stripEmojis(String text) {
        if (text == null) return text;
        // Remove emoji ranges and variation selectors
        return text.replaceAll("[\\x{1F300}-\\x{1FFFF}\\x{2600}-\\x{27BF}\\x{FE00}-\\x{FE0F}]", "").trim();
    }

    // Wrap raw content in our text template.
    public static String format(String content, String wish) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeader());
        sb.append("\n");
        sb.append(content);
        sb.append("\n");
        sb.append(getFooter(wish));
        return sb.toString();
    }
}
