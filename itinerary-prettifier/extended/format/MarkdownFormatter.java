package extended.format;

import extended.Flight;
import extended.ProcessingOptions;
import java.util.List;

// Markdown output with emojis and branding.
public class MarkdownFormatter {

    private static final String HEADER = """
            # ✈️ Anywhere ☀️ Holidays ✈️
            
            *Your Flight Details — "Your journey starts here"*
            
            ---
            """;

    private static final String FOOTER_TEMPLATE = """
            *%s*
            
            > Booked online through [Anywhere Holidays](https://nooremarendaja.kood.tech)
            """;

    // Constants for emoji indicators
    public static final String EMOJI_DEPARTURE = "🛫";
    public static final String EMOJI_ARRIVAL = "🛬";
    public static final String EMOJI_CITY = "🏙️";
    public static final String EMOJI_ADDRESS = "📍";
    public static final String EMOJI_DATE = "📅";
    public static final String EMOJI_TIME = "🕐";
    public static final String EMOJI_DURATION = "⏱️";

    public static String getHeader() {
        return HEADER;
    }

    public static String getFooter(String wish) {
        return String.format(FOOTER_TEMPLATE, wish);
    }

    public static String getSeparator() {
        return "---\n";
    }

    // Makes a nice formatted line with emoji + bold label.
    public static String formatField(String emoji, String label, String value) {
        return String.format("%s **%s:** %s\n", emoji, label, value);
    }

    // Convert flight data into Markdown format
    public static String format(List<Flight> flights, String wish, ProcessingOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER);
        sb.append("\n");

        java.time.ZoneId targetZone = null;
        if (options.getTimezone() != null) {
            try {
                targetZone = java.time.ZoneId.of(options.getTimezone());
            } catch (Exception e) {
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
            sb.append(String.format("🛫 **Departure:** %s\n", flight.getDepartureName()));
            sb.append(String.format("🏙️ **City:** %s %s\n", flight.getDepartureCity(), extended.CountryFlags.getFlag(flight.getDepartureCountry())));
            if (flight.getDepartureAddress() != null) {
                sb.append(String.format("📍 **Address:** %s\n", flight.getDepartureAddress()));
            }

            sb.append("---\n\n");

            sb.append(String.format("🛬 **Arrival:** %s\n", flight.getArrivalName()));
            sb.append(String.format("🏙️ **City:** %s %s\n", flight.getArrivalCity(), extended.CountryFlags.getFlag(flight.getArrivalCountry())));
            if (flight.getArrivalAddress() != null) {
                sb.append(String.format("📍 **Address:** %s\n", flight.getArrivalAddress()));
            }

            sb.append("---\n\n");

            if (flight.getDepartureTime() != null) {
                java.time.ZonedDateTime dTime = (targetZone != null) ? flight.getDepartureTime().withZoneSameInstant(targetZone) : flight.getDepartureTime();
                java.time.ZonedDateTime aTime = (targetZone != null) && flight.getArrivalTime() != null ? flight.getArrivalTime().withZoneSameInstant(targetZone) : flight.getArrivalTime();

                // Get timezone abbreviations (e.g., CET, EET, EST)
                String depTz = lib.TimezoneAbbreviations.getAbbreviation(dTime);
                String arrTz = aTime != null ? lib.TimezoneAbbreviations.getAbbreviation(aTime) : "";

                sb.append(String.format("📅 **Departure Time:** %s %s\n", dTime.format(fmt), depTz));

                if (aTime != null) {
                    sb.append(String.format("📅 **Arrival Time:**   %s %s\n", aTime.format(fmt), arrTz));
                }
                sb.append("\n---\n\n");

                if (aTime != null) {
                    java.time.Duration dur = java.time.Duration.between(flight.getDepartureTime(), flight.getArrivalTime());
                    long days = dur.toDays();
                    long hours = dur.toHoursPart();
                    long minutes = dur.toMinutesPart();
                    String durStr = days > 0
                            ? String.format("%dd %dh %02dm", days, hours, minutes)
                            : String.format("%dh %02dm", hours, minutes);
                    sb.append(String.format("⏱️ **Duration:**       %s\n\n", durStr));
                }
            }

            sb.append("---\n\n");

        }

        sb.append(getFooter(wish));
        return sb.toString();
    }

    // Wrap raw content in our Markdown template.
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
