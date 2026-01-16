package extended;

import java.time.ZonedDateTime;

// One flight segment - from A to B with times and locations.
public class Flight {
    
    private String departureCode;
    private String arrivalCode;
    private String departureName;
    private String arrivalName;
    private String departureCity;
    private String arrivalCity;
    private String departureCountry;
    private String arrivalCountry;
    private String departureAddress;
    private String arrivalAddress;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;
    
    public String getDepartureCode() { return departureCode; }
    public void setDepartureCode(String val) { this.departureCode = val; }
    
    public String getArrivalCode() { return arrivalCode; }
    public void setArrivalCode(String val) { this.arrivalCode = val; }
    
    public String getDepartureName() { return departureName; }
    public void setDepartureName(String val) { this.departureName = val; }
    
    public String getArrivalName() { return arrivalName; }
    public void setArrivalName(String val) { this.arrivalName = val; }
    
    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String val) { this.departureCity = val; }
    
    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String val) { this.arrivalCity = val; }
    
    public String getDepartureCountry() { return departureCountry; }
    public void setDepartureCountry(String val) { this.departureCountry = val; }
    
    public String getArrivalCountry() { return arrivalCountry; }
    public void setArrivalCountry(String val) { this.arrivalCountry = val; }
    
    public String getDepartureAddress() { return departureAddress; }
    public void setDepartureAddress(String val) { this.departureAddress = val; }
    
    public String getArrivalAddress() { return arrivalAddress; }
    public void setArrivalAddress(String val) { this.arrivalAddress = val; }
    
    public ZonedDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(ZonedDateTime val) { this.departureTime = val; }
    
    public ZonedDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(ZonedDateTime val) { this.arrivalTime = val; }
    
    // How long is this flight?
    public java.time.Duration getDuration() {
        if (departureTime == null || arrivalTime == null) return java.time.Duration.ZERO;
        return java.time.Duration.between(departureTime, arrivalTime);
    }
}
