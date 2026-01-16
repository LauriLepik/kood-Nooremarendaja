package extended;

import java.time.Duration;

// Stats and summaries for verbose mode.
public class FlightAnalyzer {
    
    // Builds the stats block shown at the end.
    public static String getStats(java.util.List<Flight> flights, int apiAddressCalls, int apiTimezoneCalls) {
        int count = flights.size();
        long totalMinutes = 0;
        int withTime = 0;
        java.util.Set<String> airports = new java.util.HashSet<>();
        
        for (Flight f : flights) {
            if (f.getDepartureCode() != null) airports.add(f.getDepartureCode());
            if (f.getArrivalCode() != null) airports.add(f.getArrivalCode());
            
            if (f.getDepartureTime() != null && f.getArrivalTime() != null) {
                totalMinutes += Math.abs(Duration.between(f.getDepartureTime(), f.getArrivalTime()).toMinutes());
                withTime++;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("API Address Calls:  %d\n", apiAddressCalls));
        if (apiTimezoneCalls > 0) {
            sb.append(String.format("API Timezone Calls: %d\n", apiTimezoneCalls));
        }
        sb.append(String.format("Flights Processed:  %d\n", count));
        sb.append(String.format("Unique Airports:    %d\n", airports.size()));
        
        if (withTime > 0) {
            long days = totalMinutes / (24 * 60);
            long hours = (totalMinutes % (24 * 60)) / 60;
            long mins = totalMinutes % 60;
            
            sb.append("Total Air Time:     ");
            if (days > 0) sb.append(days).append("d ");
            sb.append(hours).append("h ").append(mins).append("m\n");
            
            long avg = totalMinutes / withTime;
            sb.append(String.format("Avg Flight Time:    %dh %02dm\n", avg / 60, avg % 60));
        }
        
        sb.append("--------------------------------------------------");
        
        return sb.toString();
    }
}
