package extended;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Does the heavy lifting for extended mode - enriches data and adds colors.
public class ExtendedProcessor {
    
    private final ProcessingOptions options;
    private final AirportLookup lookup;
    private int apiAddressCalls = 0;
    private int apiTimezoneCalls = 0;
    
    public ExtendedProcessor(ProcessingOptions opts, AirportLookup lookup) {
        this.options = opts;
        this.lookup = lookup;
    }

    // Wraps processed text in the chosen format.
    public String enhance(String processed) {
        String wish = Branding.getRandomWish();
        
        return switch (options.getOutputType().toLowerCase()) {
            case "md", "markdown" -> extended.format.MarkdownFormatter.format(processed, wish);
            case "html" -> extended.format.HtmlFormatter.format(processed, wish);
            case "htmlf", "htmlfancy" -> extended.format.HtmlFancyFormatter.format(processed, wish);
            default -> extended.format.TextFormatter.format(processed, wish);
        };
    }
    
    // Builds the full output document from flight data.
    public String generate(List<Flight> flights) {
        enrich(flights);
        
        return switch (options.getOutputType().toLowerCase()) {
            case "md", "markdown" -> extended.format.MarkdownFormatter.format(flights, Branding.getRandomWish(), options);
            case "html" -> extended.format.HtmlFormatter.format(flights, Branding.getRandomWish(), options);
            case "htmlf", "htmlfancy" -> extended.format.HtmlFancyFormatter.format(flights, Branding.getRandomWish(), options);
            default -> extended.format.TextFormatter.format(flights, Branding.getRandomWish(), options);
        };
    }
    
    private void enrich(List<Flight> flights) {
        if (options.isVerbose()) {
            System.out.println(ConsoleUtils.ok("Enriching flight data..."));
        }
        
        for (Flight f : flights) {
            enrichLocation(f, f.getDepartureCode(), true);
            enrichLocation(f, f.getArrivalCode(), false);
        }
    }
    
    private void enrichLocation(Flight f, String code, boolean isDeparture) {
        if (code == null) return;
        
        String coords = lookup.getCoordinates(code);
        if (coords == null) return;
        
        String[] parts = coords.split(",");
        if (parts.length < 2) return;
        
        try {
            double lon = Double.parseDouble(parts[0].trim());
            double lat = Double.parseDouble(parts[1].trim());
            
            // Try online API first, fall back to offline library
            String tz = null;
            if (options.getApiKey() != null && !options.getApiKey().isEmpty()) {
                tz = GeoService.getTimezone(lat, lon, options.getApiKey());
                if (tz != null) apiTimezoneCalls++;
            }
            
            // No API key or call failed? Use the offline database.
            if (tz == null) {
                 tz = lib.TimezoneMapper.latLngToTimezoneString(lat, lon);
                 if ("unknown".equalsIgnoreCase(tz)) tz = null;
            }
            
            if (options.isVerbose() && tz != null) {
                 System.out.println("  " + code + " Timezone: " + tz);
            }
            
            // Look up address using airport name (works better than coords)
            String searchName = isDeparture ? f.getDepartureName() : f.getArrivalName();
            if (searchName == null || searchName.isEmpty()) {
                searchName = (isDeparture ? f.getDepartureCity() : f.getArrivalCity()) + " Airport";
            }

            String addr = GeoService.getAddress(searchName);
            if (addr != null) {
                apiAddressCalls++;
                
                // Strip out redundant stuff like city name appearing twice
                String city = isDeparture ? f.getDepartureCity() : f.getArrivalCity();
                String countryCode = isDeparture ? f.getDepartureCountry() : f.getArrivalCountry();
                String airportName = isDeparture ? f.getDepartureName() : f.getArrivalName();
                
                addr = cleanAddress(addr, city, countryCode, airportName);
                
                if (isDeparture) f.setDepartureAddress(addr);
                else f.setArrivalAddress(addr);
                
                if (options.isVerbose()) System.out.println("  " + code + " Address: " + addr);
                
                // Be nice to Nominatim - they ask for 1 req/sec
                Thread.sleep(1100);
            }
        } catch (Exception e) {
            // Something went wrong, but we keep going
        }
    }

    // Removes duplicate city/country/airport from address strings.
    private String cleanAddress(String fullObj, String city, String countryCode, String airportName) {
        if (fullObj == null) return null;
        
        String[] parts = fullObj.split(", ");
        List<String> clean = new ArrayList<>();
        
        String countryName = "";
        if (countryCode != null) {
            countryName = new Locale("", countryCode).getDisplayCountry(Locale.ENGLISH);
        }
        
        // Extract words from airport name for matching
        String[] airportWords = airportName != null ? airportName.toLowerCase().split("\\s+") : new String[0];
        
        for (String part : parts) {
            String p = part.trim();
            String pLower = p.toLowerCase();
            String pNorm = normalize(p);
            boolean redundant = false;
            
            // Skip city name
            if (!normalize(city).isEmpty() && pNorm.equals(normalize(city))) redundant = true;
            
            // Skip country name or code
            if (!normalize(countryName).isEmpty() && pLower.equals(countryName.toLowerCase())) redundant = true;
            if (!normalize(countryCode).isEmpty() && pNorm.equals(normalize(countryCode))) redundant = true;
            
            
            // Skip if it contains airport name words AND "airport"
            if (pLower.contains("airport")) {
                for (String word : airportWords) {
                    if (word.length() > 3 && pLower.contains(word)) {
                        redundant = true;
                        break;
                    }
                }
            }
            
            if (!redundant) {
                clean.add(p);
            }
        }
        
        return String.join(", ", clean);
    }
    
    private String normalize(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
    
    // Makes the console output pretty with colors.
    public String colorize(String text) {
        String result = text;
        
        // Colorize dates
        result = result.replaceAll("(\\d{2} [A-Za-z]+ \\d{4})", ConsoleUtils.date("$1"));
        
        // Colorize times and offsets
        result = result.replaceAll("(\\d{2}:\\d{2}(?: \\(\\+\\d+\\))?)", ConsoleUtils.time("$1"));
        result = result.replaceAll("(UTC[+-]\\d{2}:\\d{2})", ConsoleUtils.offset("$1"));
        
        // Colorize durations
        result = result.replaceAll("(?<!\\[)\\b(\\d+d )?(\\d+h )?(\\d+m)\\b", ConsoleUtils.duration("$0"));
        
        // Style arrows
        result = result.replaceAll(" → ", ConsoleUtils.WHITE + " → " + ConsoleUtils.RESET);
        
        return result;
    }
    
    public void printWarnings(List<Flight> flights) {
        List<String> unknown = new ArrayList<>();
        for (Flight f : flights) {
            checkUnknown(f.getDepartureCode(), f.getDepartureName(), unknown);
            checkUnknown(f.getArrivalCode(), f.getArrivalName(), unknown);
        }
        
        if (!unknown.isEmpty()) {
            System.out.println(ConsoleUtils.warning(
                unknown.size() + " unidentified airports in the itinerary: " + String.join(", ", unknown)));
        }
    }
    
    private void checkUnknown(String code, String name, List<String> unknown) {
        if (name != null && name.contains("Unknown Airport") && !unknown.contains(code)) {
            unknown.add(code);
        }
    }
    
    public void printStats(List<Flight> flights) {
        if (options.isVerbose()) {
            System.out.println(ConsoleUtils.ok(FlightAnalyzer.getStats(flights, apiAddressCalls, apiTimezoneCalls)));
        }
    }
}
