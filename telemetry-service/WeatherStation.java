package sprint;

public class WeatherStation {
    //State list
    private Double airTemp = null;
    private Double airPressure = null;
    private Double precipitation = null;
    private Double windSpeed = null;
    private Double windDirection = null;
    private Double humidity = null;
    private Double dewPoint = null;
    private Double soilMoisture = null;
    private Double cloudCover = null;

    public void updateState(String input) {
        String[] inputDataArray = input.split("\n");
        for (String dataLine : inputDataArray) {
            String[] dataLineArray = dataLine.split(",");
            String value = dataLineArray[1];
            Double parsedValue = value.equals("NULL") ? null : Double.valueOf(value);
            
            switch (dataLineArray[0]) {
                case "1":
                    airTemp = parsedValue;
                    break;
                case "2":
                    airPressure = parsedValue;
                    break;
                case "7":
                    precipitation = parsedValue;
                    break;
                case "11":
                    windSpeed = parsedValue;
                    break;
                case "12":
                    windDirection = parsedValue;
                    break;
                case "13":
                    humidity = parsedValue;
                    break;
                case "14":
                    dewPoint = parsedValue;
                    break;
                case "15":
                    soilMoisture = parsedValue;
                    break;
                case "22":
                    cloudCover = parsedValue;
                    break;
            }
        }
    }

    public String getState() {
        return "airTemp:" + (airTemp == null ? "NULL" : airTemp) + "\n" +
               "airPressure:" + (airPressure == null ? "NULL" : airPressure) + "\n" +
               "precipitation:" + (precipitation == null ? "NULL" : precipitation) + "\n" +
               "windSpeed:" + (windSpeed == null ? "NULL" : windSpeed) + "\n" +
               "windDirection:" + (windDirection == null ? "NULL" : windDirection) + "\n" +
               "humidity:" + (humidity == null ? "NULL" : humidity) + "\n" +
               "dewPoint:" + (dewPoint == null ? "NULL" : dewPoint) + "\n" +
               "soilMoisture:" + (soilMoisture == null ? "NULL" : soilMoisture) + "\n" +
               "cloudCover:" + (cloudCover == null ? "NULL" : cloudCover) + "\n";
    }

    public void clearState() {
        airTemp = null;
        airPressure = null;
        precipitation = null;
        windSpeed = null;
        windDirection = null;
        humidity = null;
        dewPoint = null;
        soilMoisture = null;
        cloudCover = null;
    }
}
