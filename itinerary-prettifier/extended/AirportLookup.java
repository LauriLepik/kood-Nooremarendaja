package extended;

import java.io.*;
import java.util.*;

public class AirportLookup {
    
    private final Map<String, String> iataToName = new HashMap<>();
    private final Map<String, String> icaoToName = new HashMap<>();
    private final Map<String, String> iataToCity = new HashMap<>();
    private final Map<String, String> icaoToCity = new HashMap<>();
    private final Map<String, String> iataToCoords = new HashMap<>();
    private final Map<String, String> icaoToCoords = new HashMap<>();
    private final Map<String, String> iataToCountry = new HashMap<>();
    private final Map<String, String> icaoToCountry = new HashMap<>();
    
    private boolean malformed = false;
    
    public AirportLookup(String csvPath) throws IOException {
        loadData(csvPath);
    }
    
    private void loadData(String csvPath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                malformed = true;
                return;
            }
            
            // Dynamic column index resolution
            String[] headers = parseCSVLine(headerLine);
            int nameIdx = findColumn(headers, "name");
            int icaoIdx = findColumn(headers, "icao_code");
            int iataIdx = findColumn(headers, "iata_code");
            int cityIdx = findColumn(headers, "municipality");
            int countryIdx = findColumn(headers, "iso_country");
            int coordIdx = findColumn(headers, "coordinates");
            
            if (nameIdx == -1 || icaoIdx == -1 || iataIdx == -1 || 
                cityIdx == -1 || countryIdx == -1 || coordIdx == -1) {
                malformed = true;
                return;
            }
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = parseCSVLine(line);
                
                // Row doesn't have enough columns, skip it
                if (fields.length <= Math.max(Math.max(nameIdx, icaoIdx), 
                    Math.max(Math.max(iataIdx, cityIdx), Math.max(countryIdx, coordIdx)))) {
                    continue; 
                }
                
                String name = fields[nameIdx].trim();
                String icao = fields[icaoIdx].trim();
                String iata = fields[iataIdx].trim();
                String city = fields[cityIdx].trim();
                String country = fields[countryIdx].trim();
                String coords = fields[coordIdx].trim();
                
                // Only add if we have a name to work with
                if (!name.isEmpty()) {
                    if (!iata.isEmpty()) {
                        iataToName.put(iata, name);
                        iataToCity.put(iata, city);
                        iataToCoords.put(iata, coords);
                        iataToCountry.put(iata, country);
                    }
                    if (!icao.isEmpty()) {
                        icaoToName.put(icao, name);
                        icaoToCity.put(icao, city);
                        icaoToCoords.put(icao, coords);
                        icaoToCountry.put(icao, country);
                    }
                }
            }
        }
    }
    
    private int findColumn(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    private String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
    
    public boolean isMalformed() {
        return malformed;
    }
    
    public String getAirportName(String code) {
        return iataToName.getOrDefault(code, icaoToName.get(code));
    }
    
    public String getCityName(String code) {
        return iataToCity.getOrDefault(code, icaoToCity.get(code));
    }
    
    public String getCoordinates(String code) {
        return iataToCoords.getOrDefault(code, icaoToCoords.get(code));
    }
    
    public String getCountry(String code) {
        return iataToCountry.getOrDefault(code, icaoToCountry.get(code));
    }
}
