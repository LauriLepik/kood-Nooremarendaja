package extended;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

// Talks to external APIs for address and timezone lookups.
public class GeoService {
    
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org";
    
    // Query Nominatim for address details
    public static String getAddress(String query) {
        if (query == null || query.isEmpty()) return null;
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("%s/search?q=%s&format=json&limit=1", NOMINATIM_URL, encoded);
            
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "ItineraryPrettifier/1.0")
                .header("Accept-Language", "en-US,en;q=0.5")
                .GET().build();
            
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            
            if (res.statusCode() == 200) {
                String body = res.body();
                // Simple JSON field extraction to avoid heavy dependencies
                int idx = body.indexOf("\"display_name\"");
                if (idx != -1) {
                    int start = body.indexOf("\"", idx + 14) + 1;
                    int end = body.indexOf("\"", start);
                    return body.substring(start, end);
                }
            }
        } catch (Exception e) {
            // Network problem, just return null and move on
        }
        return null;
    }
    
    // Query Geoapify for timezone information
    public static String getTimezone(double lat, double lon, String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) return null;
        
        try {
            String url = String.format(
                "https://api.geoapify.com/v1/geocode/reverse?lat=%f&lon=%f&apiKey=%s",
                lat, lon, apiKey);
            
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            
            if (res.statusCode() == 200) {
                String body = res.body();
                int idx = body.indexOf("\"timezone\"");
                if (idx != -1) {
                    int nameIdx = body.indexOf("\"name\"", idx);
                    if (nameIdx != -1) {
                        int start = body.indexOf("\"", nameIdx + 6) + 1;
                        int end = body.indexOf("\"", start);
                        return body.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            // Didn't work, we'll use the offline fallback instead
        }
        return null;
    }
}
