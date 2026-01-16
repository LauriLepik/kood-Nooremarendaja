package extended.format;

import extended.CountryFlags;
import extended.Flight;
import extended.ProcessingOptions;
import java.util.List;

// Regular HTML output with emojis and branding.
public class HtmlFormatter {
    
    private static final String HEADER = """
            <h1>✈️ Anywhere ☀️ Holidays ✈️</h1>
            <p><em>Your Flight Details — "Your journey starts here"</em></p>
            <hr>
            """;
    
    private static final String FOOTER_TEMPLATE = """
            <p><em>%s</em></p>
            <p>Booked online through <a href="https://nooremarendaja.kood.tech">Anywhere Holidays</a></p>
            """;
    
    public static String format(String processed, String wish) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'>");
        sb.append("<style>body { font-family: sans-serif; line-height: 1.6; padding: 20px; max-width: 800px; margin: 0 auto; }</style>");
        sb.append("</head><body>");
        sb.append(HEADER);
        sb.append(processed.replace("\n", "<br>"));
        sb.append(String.format(FOOTER_TEMPLATE, wish));
        sb.append("</body></html>");
        return sb.toString();
    }

    public static String format(List<Flight> flights, String wish, ProcessingOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset='UTF-8'>");
        sb.append("<style>body { font-family: sans-serif; line-height: 1.6; padding: 20px; max-width: 800px; margin: 0 auto; } ");
        sb.append(".flight { margin-bottom: 30px; } hr { margin: 20px 0; border: none; border-top: 1px solid #ccc; }</style>");
        sb.append("</head><body>");
        sb.append(HEADER);
        
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
        
        java.time.ZoneId targetZone = null;
        if (options.getTimezone() != null) {
            try {
                targetZone = java.time.ZoneId.of(options.getTimezone());
            } catch (Exception e) { 
                // Bad timezone, ignore it
            }
        }
        
        for (Flight f : flights) {
            sb.append("<div class='flight'>");
            
            // Departure
            sb.append("<p>🛫 <strong>Departure:</strong> ").append(f.getDepartureName()).append("</p>");
            sb.append("<p>🏙️ <strong>City:</strong> ").append(f.getDepartureCity()).append(" ").append(CountryFlags.getFlag(f.getDepartureCountry())).append("</p>");
            if (f.getDepartureAddress() != null) {
                sb.append("<p>📍 <strong>Address:</strong> ").append(f.getDepartureAddress()).append("</p>");
            }
            
            sb.append("<hr>");
            
            // Arrival
            sb.append("<p>🛬 <strong>Arrival:</strong> ").append(f.getArrivalName()).append("</p>");
            sb.append("<p>🏙️ <strong>City:</strong> ").append(f.getArrivalCity()).append(" ").append(CountryFlags.getFlag(f.getArrivalCountry())).append("</p>");
            if (f.getArrivalAddress() != null) {
                sb.append("<p>📍 <strong>Address:</strong> ").append(f.getArrivalAddress()).append("</p>");
            }
            
            sb.append("<hr>");
            
            // Times - convert to target timezone if specified
            if (f.getDepartureTime() != null) {
                java.time.ZonedDateTime dTime = (targetZone != null) ? f.getDepartureTime().withZoneSameInstant(targetZone) : f.getDepartureTime();
                String depTz = lib.TimezoneAbbreviations.getAbbreviation(dTime);
                sb.append("<p>🕒 <strong>Departure:</strong> ").append(dTime.format(fmt))
                  .append(" <span style='color:#888'>").append(depTz).append("</span></p>");
            }
            if (f.getArrivalTime() != null) {
                java.time.ZonedDateTime aTime = (targetZone != null) ? f.getArrivalTime().withZoneSameInstant(targetZone) : f.getArrivalTime();
                String arrTz = lib.TimezoneAbbreviations.getAbbreviation(aTime);
                sb.append("<p>🕒 <strong>Arrival:</strong> ").append(aTime.format(fmt))
                  .append(" <span style='color:#888'>").append(arrTz).append("</span></p>");
            }
            if (f.getDepartureTime() != null && f.getArrivalTime() != null) {
                java.time.Duration dur = java.time.Duration.between(f.getDepartureTime(), f.getArrivalTime());
                long days = dur.toDays();
                long hours = dur.toHoursPart();
                long minutes = dur.toMinutesPart();
                String durStr = days > 0 
                    ? String.format("%dd %dh %02dm", days, hours, minutes)
                    : String.format("%dh %02dm", hours, minutes);
                sb.append("<p>⏱️ <strong>Duration:</strong> ").append(durStr).append("</p>");
            }

            sb.append("</div>");
            sb.append("<hr>");
        }
        
        sb.append(String.format(FOOTER_TEMPLATE, wish));
        sb.append("</body></html>");
        return sb.toString();
    }
}
