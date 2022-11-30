import java.util.*;
import java.io.File;

public class MBTA {

    private Map<String, List<Station>>    lines;
    private Map<Passenger, List<Station>> journeys;

    // Creates an initially empty simulation
    public MBTA() {
        lines    = new LinkedHashMap<>();
        journeys = new LinkedHashMap<>();
    }

    // Adds a new transit line with given name and stations
    public void addLine(String name, List<String> stations) {

        if (lines.containsKey(name)) {
            throw new RuntimeException(String.format("Line \"%s\" already exists. Duplicates are prohibited.", name));
        }

        List<Station> newStations = stations.stream()
            .map(e -> Station.make(e))
            .toList();

        lines.put(name, newStations);
    }

    // Adds a new planned journey to the simulation
    public void addJourney(String name, List<String> stations) {
        Passenger p = Passenger.make(name);
        if (journeys.containsKey(p)) {
            throw new RuntimeException(String.format("%s's journey already exists. Duplicates are prohibited.", name));
        }

        List<Station> path = stations.stream()
            .map(e -> Station.make(e))
            .toList();

        journeys.put(p, path);
    }

    // Return normally if initial simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkStart() {
    }

    // Return normally if final simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkEnd() {
    }

    // reset to an empty simulation
    public void reset() {
        lines    = new LinkedHashMap<>();
        journeys = new LinkedHashMap<>();
    }

    // adds simulation configuration from a file
    public void loadConfig(String filename) {
        Config c = Config.fromFile(new File(filename));

        c.lines.entrySet().stream()
            .forEach(e -> addLine(e.getKey(), e.getValue()));

        c.trips.entrySet().stream()
            .forEach(e -> addJourney(e.getKey(), e.getValue()));
    }
}
