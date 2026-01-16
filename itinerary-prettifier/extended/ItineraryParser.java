package extended;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Parses itinerary text into structured Flight objects.
public class ItineraryParser {

    private final AirportLookup lookup;

    public ItineraryParser(AirportLookup lookup) {
        this.lookup = lookup;
    }

    public List<Flight> parse(String content) {
        List<Flight> flights = new ArrayList<>();

        List<String> airportCodes = new ArrayList<>();
        List<ZonedDateTime> times = new ArrayList<>();

        // Scan for all airport codes (IATA or ICAO)
        Matcher mCode = Pattern.compile("(?<!\\*)#{1,2}([A-Z]{3,4})").matcher(content);
        while (mCode.find()) {
            airportCodes.add(mCode.group(1));
        }

        // Scan for all timestamp markers
        Matcher mTime = Pattern.compile("[DT](?:12|24)?\\(([^)]+)\\)").matcher(content);
        while (mTime.find()) {
            ZonedDateTime zdt = null;
            try {
                // Ensure we have a valid ISO timestamp containing a time component
                String val = mTime.group(1);
                if (val.contains("T")) {
                    zdt = ZonedDateTime.parse(val);
                }
            } catch (Exception e) {
                // Invalid date format, keeping zdt as null
            }
            times.add(zdt);
        }

        // Pair the discovered codes and times into logical flights
        int codeIdx = 0;
        int timeIdx = 0;

        while (codeIdx + 1 < airportCodes.size()) {
            Flight f = new Flight();

            String dep = airportCodes.get(codeIdx++);
            String arr = airportCodes.get(codeIdx++);

            f.setDepartureCode(dep);
            f.setArrivalCode(arr);

            if (lookup != null) {
                enrich(f, dep, true);
                enrich(f, arr, false);
            }

            // Assign times if available, assuming order matches the flight segments
            if (timeIdx + 1 < times.size()) {
                f.setDepartureTime(times.get(timeIdx++));
                f.setArrivalTime(times.get(timeIdx++));
            }

            flights.add(f);
        }

        return flights;
    }

    private void enrich(Flight f, String code, boolean isDep) {
        String name = lookup.getAirportName(code);
        String city = lookup.getCityName(code);
        String country = lookup.getCountry(code);

        if (isDep) {
            f.setDepartureName(name);
            f.setDepartureCity(city);
            f.setDepartureCountry(country);
        } else {
            f.setArrivalName(name);
            f.setArrivalCity(city);
            f.setArrivalCountry(country);
        }
    }
}
