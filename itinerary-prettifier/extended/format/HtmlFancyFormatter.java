package extended.format;

import extended.CountryFlags;
import extended.Flight;
import extended.ProcessingOptions;
import java.util.List;

public class HtmlFancyFormatter {
    
    private static final String CSS = """
            * { box-sizing: border-box; }
            body { font-family: 'Inter', sans-serif; margin: 0; padding: 40px; min-height: 100vh;
                   background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
            .container { max-width: 500px; margin: 0 auto; }
            .header { background: rgba(255,255,255,0.95); border-radius: 16px 16px 0 0; padding: 24px; text-align: center;
                      box-shadow: 0 4px 20px rgba(0,0,0,0.2); }
            .header h1 { margin: 0; font-size: 1.5em; color: #333; }
            .header .tagline { color: #666; margin: 8px 0 0; font-size: 0.9em; }
            .flight-card { background: white; margin-top: 2px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); border-radius: 4px; overflow: hidden; }
            .section { background: white; padding: 20px 24px; }
            .section-title { font-weight: 700; color: #764ba2; margin-bottom: 12px; font-size: 0.85em; text-transform: uppercase; }
            .airport { font-size: 1.1em; font-weight: 600; color: #333; }
            .city { color: #1565c0; }
            .address { color: #888; font-size: 0.85em; margin-top: 4px; }
            .arrow { text-align: center; background: white; padding: 8px; font-size: 1.5em; color: #764ba2; }
            .time-section { background: white; padding: 20px 24px; margin-top: 2px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 20px rgba(0,0,0,0.1); border-radius: 4px; }
            .flight-divider { background: rgba(255,255,255,0.95); padding: 12px; text-align: center; font-size: 1.2em; color: #764ba2; box-shadow: 0 4px 20px rgba(0,0,0,0.1); margin-top: 2px; margin-bottom: 2px; border-top: 1px solid #e0e0e0; border-bottom: 1px solid #e0e0e0; }
            .time-block { text-align: center; }
            .time-label { color: #888; font-size: 0.85em; text-transform: uppercase; margin-bottom: 4px; }
            .time-value { font-size: 1.1em; font-weight: bold; color: #333; }
            .tz { color: #888; font-size: 0.8em; margin-top: 2px; }
            .duration { background: #e7f3ff; color: #764ba2; padding: 8px 16px; border-radius: 20px; font-weight: bold; display: inline-block; }
            .footer { background: rgba(255,255,255,0.95); border-radius: 0 0 16px 16px; padding: 20px; text-align: center;
                      box-shadow: 0 4px 20px rgba(0,0,0,0.2); margin-top: 2px; }
            .wish { font-style: italic; color: #333; margin-bottom: 8px; }
            .branding { color: #666; font-size: 0.85em; }
            .branding a { color: #764ba2; text-decoration: none; font-weight: 600; }
            .total-box { background: rgba(255,255,255,0.95); margin-top: 2px; padding: 16px; text-align: center;
                         box-shadow: 0 4px 20px rgba(0,0,0,0.1); border-radius: 4px; border-top: 1px solid #e0e0e0; }
            .total-label { color: #764ba2; font-size: 0.85em; text-transform: uppercase; font-weight: 700; margin-bottom: 12px; }
            .total-value { font-size: 1.3em; font-weight: 700; color: #333; }
            """;

    private static final String HEAD = """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="UTF-8">
            <title>Flight Itinerary - Anywhere Holidays</title>
            <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
            <style>%s</style>
            </head>
            <body>
            <div class="container">
            <div class="header">
            <h1>✈️ Anywhere ☀️ Holidays ✈️</h1>
            <p class="tagline">Your Flight Details - "Your journey starts here"</p>
            </div>
            """;

    public static String format(String processed, String wish) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(HEAD, CSS));
        sb.append("<div class='section'>").append(processed.replace("\n", "<br>")).append("</div>");
        sb.append("<div class='footer'>");
        sb.append("<p class='wish'>").append(wish).append("</p>");
        sb.append("<p class='branding'>Booked online through <a href='https://nooremarendaja.kood.tech'>Anywhere Holidays</a></p>");
        sb.append("</div></div></body></html>");
        return sb.toString();
    }

    public static String format(List<Flight> flights, String wish, ProcessingOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(HEAD, CSS));
        
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
            }
        }
        
        boolean first = true;
        for (Flight f : flights) {
            // Add divider between flights (not before first)
            if (!first) {
                sb.append("<div class='flight-divider'>🛫 ✈️ ✈️ ✈️ 🛬</div>");
            }
            first = false;
            
            // Start flight card
            sb.append("<div class='flight-card'>");
            
            // Departure section
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>🛫 Departure</div>");
            sb.append("<div class='airport'>").append(f.getDepartureName()).append("</div>");
            sb.append("<div class='city'>").append(f.getDepartureCity()).append(" ").append(CountryFlags.getFlag(f.getDepartureCountry())).append("</div>");
            if (f.getDepartureAddress() != null) {
                sb.append("<div class='address'>").append(f.getDepartureAddress()).append("</div>");
            }
            sb.append("</div>");
            
            // Arrow
            sb.append("<div class='arrow'>↓</div>");
            
            // Arrival section
            sb.append("<div class='section'>");
            sb.append("<div class='section-title'>🛬 Arrival</div>");
            sb.append("<div class='airport'>").append(f.getArrivalName()).append("</div>");
            sb.append("<div class='city'>").append(f.getArrivalCity()).append(" ").append(CountryFlags.getFlag(f.getArrivalCountry())).append("</div>");
            if (f.getArrivalAddress() != null) {
                sb.append("<div class='address'>").append(f.getArrivalAddress()).append("</div>");
            }
            sb.append("</div>");
            
            // End flight card
            sb.append("</div>");
            
            // Time section - vertical blocks (Separate card with spacer)
            if (f.getDepartureTime() != null && f.getArrivalTime() != null) {
                java.time.ZonedDateTime dTime = (targetZone != null) ? f.getDepartureTime().withZoneSameInstant(targetZone) : f.getDepartureTime();
                java.time.ZonedDateTime aTime = (targetZone != null) ? f.getArrivalTime().withZoneSameInstant(targetZone) : f.getArrivalTime();
                String depTz = lib.TimezoneAbbreviations.getAbbreviation(dTime);
                String arrTz = lib.TimezoneAbbreviations.getAbbreviation(aTime);
                
                sb.append("<div class='time-section'>");
                sb.append("<div class='time-block'><div class='time-label'>Departure</div><div class='time-value'>").append(dTime.format(fmt)).append("</div><div class='tz'>").append(depTz).append("</div></div>");
                sb.append("<div class='time-block'><div class='time-label'>Arrival</div><div class='time-value'>").append(aTime.format(fmt)).append("</div><div class='tz'>").append(arrTz).append("</div></div>");
                
                java.time.Duration dur = java.time.Duration.between(f.getDepartureTime(), f.getArrivalTime());
                long days = dur.toDays();
                long hours = dur.toHoursPart();
                long minutes = dur.toMinutesPart();
                String durStr;
                if (days > 0) {
                     durStr = (minutes > 0) ? String.format("%dd %dh %02dm", days, hours, minutes) 
                                            : String.format("%dd %dh", days, hours);
                } else {
                     durStr = String.format("%dh %02dm", hours, minutes);
                }
                sb.append("<div class='time-block'><span class='duration'>").append(durStr).append("</span></div>");
                sb.append("</div> <!-- end time section -->");
            }
        }
        
        // Calculate and add Total Journey Time
        java.time.Duration totalDur = java.time.Duration.ZERO;
        for (Flight f : flights) {
            if (f.getDepartureTime() != null && f.getArrivalTime() != null) {
                totalDur = totalDur.plus(java.time.Duration.between(f.getDepartureTime(), f.getArrivalTime()));
            }
        }
        
        if (!totalDur.isZero()) {
            long days = totalDur.toDays();
            long hours = totalDur.toHoursPart();
            long minutes = totalDur.toMinutesPart();
            String totalStr;
            if (days > 0) {
                 totalStr = (minutes > 0) ? String.format("%dd %dh %02dm", days, hours, minutes) 
                                          : String.format("%dd %dh", days, hours);
            } else {
                 totalStr = String.format("%dh %02dm", hours, minutes);
            }
            
            sb.append("<div class='total-box'>");
            sb.append("<div class='total-label'>Total Journey Time</div>");
            sb.append("<div class='total-value'><span class='duration'>").append(totalStr).append("</span></div>");
            sb.append("</div>");
        }
        
        sb.append("<div class='footer'>");
        // Wrap emojis in non-italic span so they don't look weird, capture variation selectors
        String fixedWish = wish.replaceAll("([\\x{1F300}-\\x{1FFFF}\\x{2600}-\\x{27BF}]\\x{FE0F}?)", "<span style='font-style:normal'>$1</span>");
        sb.append("<p class='wish'>").append(fixedWish).append("</p>");
        sb.append("<p class='branding'>Booked online through <a href='https://nooremarendaja.kood.tech'>Anywhere Holidays</a></p>");
        sb.append("</div>");
        sb.append("</div></body></html>");
        return sb.toString();
    }
}
