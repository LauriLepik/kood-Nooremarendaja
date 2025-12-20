# Itinerary Prettifier (WIP) ✈️

> **Status**: 🚧 Work In Progress
> **Deadline**: 11.1.2026 23:59

A command-line tool designed to transform raw, system-generated flight itineraries into customer-friendly formats. This utility parses text files, converts airport codes (IATA/ICAO) to city names, formats ISO 8601 dates, and cleans up spacing.

## 📋 Project Overview

**"Anywhere Holidays"** needs a short-term solution to help back-office administrators. Currently, flight booking systems generate raw text itineraries that are hard to read. This tool automates the "prettifying" process, saving administrators time and ensuring consistent output for customers.

### Functional Requirements

- **Input/Output**: Reads from a text file and writes to a new file.
- **Airport Lookup**: dynamically replaces codes like `#LAX` or `##EGLL` with real airport names using an external CSV.
- **Date/Time Formatting**: Converts ISO dates (e.g., `D(2007-04-05...)`) into readable formats (e.g., `05 Apr 2007` or `12:30PM`).
- **Cleanup**: Trims character artifacts and excessive vertical whitespace.

## 🚀 Usage

```bash
java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv
```

### Arguments

1. **Input Path**: Path to the raw itinerary file.
2. **Output Path**: Destination for the prettified file.
3. **Airport Lookup**: Path to the CSV database of airport codes.

### Flags

- `-h`: Display usage help.

```text
itinerary usage:
$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv
```

## 🛠 Features (Planned)

- [ ] **Airport Code Expansion**:
  - `#ABC` -> IATA Code (3 letters)
  - `##ABCD` -> ICAO Code (4 letters)
  - `*#ABC` -> City Name lookup
- [ ] **Smart Date Formatting**:
  - `D(...)` -> `DD-Mmm-YYYY`
  - `T12(...)` -> `12:30PM (-02:00)`
  - `T24(...)` -> `12:30 (-02:00)`
  - `Z` offsets -> `(+00:00)`
- [ ] **CSV Parsing**: Robust handling of dynamic column orders in `airport-lookup.csv`.
- [ ] **Error Handling**: Graceful failure messages for missing files or malformed data.

## 📂 Project Structure

```text
itinerary-prettifier/
├── Prettifier.java       # Main entry point (WIP)
├── airport-lookup.csv    # Data source for airport codes
├── input.txt             # Sample raw itinerary
└── README.md             # This documentation
```

## 📝 Resources

- [Airport Codes CSV Details - Provided by //kood](https://kood.tech) (Mock link based on requirements)

## Creator 👨‍💻

**[Lauri Lepik](https://github.com/LauriLepik)**

---
*Developed as a solo project for the **[kood/Nooremarendaja](https://kood.tech/kood-nooremarendaja/)** curriculum (Free, **[NextGenEU](https://kood.tech/meist/toetused/)** funded, 5-month intensive).*
