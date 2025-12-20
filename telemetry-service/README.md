# Telemetry Data Service (Weather Station) 📡

An optimized backend service exercise designed to simulate **critical national infrastructure**. This project focuses on handling high-frequency telemetry data with extreme bandwidth constraints using a custom state-based compression algorithm (similar to HPACK).

## The Challenge 🛑

- **Context**: A weather monitoring system where stations send data every minute.
- **Constraint**: To minimize bandwidth, stations *only* send fields that have changed since the last transmission.
- **Objective**: Build a robust service that maintains the full state of a weather station, updating only the changed fields while preserving the integrity of unchanged data.
- **Requirement**: **No external libraries**. All state management and parsing must be implemented with raw Java logic.

## Features ✨

- **State-Based Compression Handling**: intelligently merges partial updates (deltas) into the current persisted state.
- **Data Integrity**: Accurately maps internal IDs to human-readable keys (e.g., `1` -> `airTemp`, `11` -> `windSpeed`).
- **Null-Safety**: Robustly handles `NULL` values for sensors that are offline or not reporting.
- **Batch Processing**: Capable of parsing multiple updates in a single payload (newline-separated CSV).

## Integration Example 🔌

The system transforms raw CSV telemetry codes into a full JSON-like state object:

**Incoming Delta (Partial Update):**

```text
11,15.5
13,32.3
```

**System Output (Full State):**

```text
airTemp:NULL
airPressure:NULL
precipitation:NULL
windSpeed:15.5    <-- Updated
windDirection:NULL
humidity:32.3     <-- Updated
...
```

## Usage 🚀

This is a backend logic class. usage involves creating an instance and feeding it raw string payloads.

```java
WeatherStation station = new WeatherStation();

// 1. Initial State (All NULL)
System.out.println(station.getState());

// 2. Receive Update (Wind Speed changed to 15.5)
station.updateState("11,15.5");

// 3. Receive Batch Update (Temp and Humidity changed)
station.updateState("1,21.6\n13,45.2");

// 4. View Full Combined State
System.out.println(station.getState());
```

## Technical Highlights 💡

- **Systems Engineering**: Simulates a real-world "Micro-service" component responsible for data normalization.
- **Optimization**: Demonstrates understanding of payload efficiency and stateful versus stateless communication.
- **Null-Safe Precision Handling**: Uses Java's `Double` wrapper class to distinguish between `0.0` (a valid reading) and `null` (sensor offline), a critical distinction in telemetry.
- **Efficient String Parsing**: Implements a manual CSV parser using `String.split` and `switch` statements, avoiding heavy external serialization libraries like Jackson or Gson.

## Creator 👨‍💻

**[Lauri Lepik](https://github.com/LauriLepik)**

---
*Developed as an advanced algorithmic exercise for the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
