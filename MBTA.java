import java.util.*;
import java.util.Map.Entry;
import java.io.File;

public class MBTA {
    
    private Map<Station, Set<Journey>> stations;
    private Map<Train, Line> lines;
    private Map<Passenger, Journey> journies;

    private Config config = null;

    // Creates an initially empty simulation
    public MBTA() { 
        stations = new HashMap<>();
        lines    = new HashMap<>();
        journies = new HashMap<>();
    }

    // Adds a new transit line with given name and stations
    public void addLine(String name, List<String> stations) {
        Train t = Train.make(name);
        List<Station> s_objs = stations.stream().map(s -> Station.make(s)).toList();
        Line l = new Line(t, s_objs);
        s_objs.stream().forEach(s -> this.stations.put(s, new HashSet<>()));
        this.lines.put(t, l);
    }

    // Adds a new planned journey to the simulation
    public void addJourney(String name, List<String> stations) {
        Passenger p = Passenger.make(name);
        List<Station> s_objs = stations.stream().map(s -> Station.make(s)).toList();
        Journey j = new Journey(p, s_objs);
        journies.put(p, j);
        this.stations.get(s_objs.get(0)).add(j);
    }

    // Return normally if initial simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkStart() {
        for (Line l : lines.values()) {
            if (!l.isAtStart()) {
                throw new RuntimeException(String.format("Train %s was not at the start of its line", l.getTrain()));
            }
        }

        for (Journey j : journies.values()) {
            if (!stations.get(j.first()).contains(j)) {
                throw new RuntimeException(String.format("Passenger %s was not at the start of its line", j.getPassenger()));
            }
        }
    }

    // Return normally if final simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkEnd() {
        for (Journey j : journies.values()) {
            if (!j.isDone()) {
                throw new RuntimeException(String.format("Passenger %s did not complete their journey", j.getPassenger()));
            }
        }
    }

    // reset to an empty simulation
    public void reset() {
        stations = new HashMap<>();
        lines    = new HashMap<>();
        journies = new HashMap<>();
    }

    // adds simulation configuration from a file
    public void loadConfig(String filename) {
        config = Config.fromFile(new File(filename));

        for (Map.Entry<String, List<String>> line : config.lines.entrySet()) {
            String line_name = line.getKey();
            List<String> station_names = line.getValue();
            addLine(line_name, station_names);
        }

        for (Map.Entry<String, List<String>> trip : config.trips.entrySet()) {
            String passenger_name = trip.getKey();
            List<String> station_names = trip.getValue();
            addJourney(passenger_name, station_names);
        }
    }

    public Station findPassengerInStation(Passenger p) {
        for (Entry<Station, Set<Journey>> e : stations.entrySet()) {
            for (Journey j : e.getValue()) {
                if (j.getPassenger() == p) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    public Train findPassengerOnTrain(Passenger p) {
        for (Entry<Train, Line> e : lines.entrySet()) {
            Train t = e.getKey();
            Line  l = e.getValue();
            if (l.findPassenger(p) != null) {
                return t;
            }
        }
        return null;
    }

    public Station findTrain(Train t) {
        return lines.get(t).find();
    }

    public Station advanceTrain(Train t) {
        return lines.get(t).advance().getStation();
    }

    public Map<Train, Station> allTrainLocs() {
        Map<Train, Station> loc = new HashMap<>();
        lines.entrySet().stream().forEach(e -> loc.put(e.getKey(), e.getValue().find()));
        return loc;
    }

    public void boardTrain(Passenger p, Train t, Station s) {
        Journey j = journies.get(p);
        if (!stations.get(s).remove(j)) {
            throw new RuntimeException(String.format("Passenger %s was not at Station %s", p, s));
        }
        j.board();
        lines.get(t).addJourney(j);
    }

    public void deboardTrain(Passenger p, Train t, Station s) {
        Journey j = journies.get(p);
        j.deboard();
        stations.get(s).add(j);
        if (findTrain(t) != s) {
            throw new RuntimeException(String.format("Train %s was not at Station %s", t, s));
        }
    }

    public Station passengerNextStation(Passenger p) {
        return journies.get(p).next();
    }
}
