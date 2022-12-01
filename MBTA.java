import java.util.*;
import java.io.File;

public class MBTA {
    private Map<Passenger, List<Station>> journeys = new HashMap<>();
    private Map<Train, List<Station>> lines = new HashMap<>();

    private Map<Passenger, Integer> p_locations = new HashMap<>();

    private Map<Train, Integer> t_locations = new HashMap<>();
    private Map<Train, Integer> t_movements = new HashMap<>();

    private Map<Station, List<Passenger>> stations = new HashMap<>();

    private Map<Train, Map<Station, List<Passenger>>> trains = new HashMap<>();

    private Config config = null;

    // Creates an initially empty simulation
    public MBTA() { }

    // Adds a new transit line with given name and stations
    public void addLine(String name, List<String> stations) {
        Train t = Train.make(name);

        if (this.lines.containsKey(t)) {
            throw new RuntimeException(String.format("Line \"%s\" already exists. Duplicates are prohibited.", name));
        } else if (stations.isEmpty()) {
            throw new RuntimeException(String.format("A line must contain stations; \"%s\" does not.", name));
        }

        this.t_locations.put(t, 0);
        this.t_movements.put(t, 1);
        
        this.lines.put(t, stations.stream().map(station -> Station.make(station)).toList());
        this.trains.put(t, new HashMap<>());

        for (Station s : lines.get(t)) {
            this.stations.put(s, new ArrayList<>());
            this.trains.get(t).put(s, new ArrayList<>());
        }
    }

    // Adds a new planned journey to the simulation
    public void addJourney(String name, List<String> stations) {
        Passenger p = Passenger.make(name);
        if (this.journeys.containsKey(p)) {
            throw new RuntimeException(String.format("%s's journey already exists. Duplicates are prohibited.", name));
        } else if (stations.isEmpty()) {
            throw new RuntimeException(String.format("A journey must contain stations; \"%s\" does not.", name));
        }

        this.journeys.put(p, stations.stream().map(s -> Station.make(s)).toList());
        this.stations.get(this.journeys.get(p).get(0)).add(p);
        this.p_locations.put(p, 0);
    }

    // Return normally if initial simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkStart() {
        for (int loc : t_locations.values()) {
            if (loc != 0) {
                throw new RuntimeException("Some train was not at the beginning of its line");
            }
        }

        for (int loc : p_locations.values()) {
            if (loc != 0) {
                throw new RuntimeException("Some passenger is not at the beginning of their journey");
            }
        }
    }

    // Return normally if final simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkEnd() {
        for (int loc : p_locations.values()) {
            if (loc != -1) {
                throw new RuntimeException("Some passenger did not complete their journey");
            }
        }
    }

    // reset to an empty simulation
    public void reset() {
        journeys    = new HashMap<>();
        lines       = new HashMap<>();
        t_locations = new HashMap<>();
        t_movements = new HashMap<>();
        stations    = new HashMap<>();
        trains      = new HashMap<>();
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

    public Station findTrain(Train t) {
        return lines.get(t).get(t_locations.get(t));
    }

    public Map<Train, Station> allTrainLocs() {
        Map<Train, Station> out_map = new HashMap<>();

        for (Map.Entry<Train, Integer> e : t_locations.entrySet()) {
            Train t = e.getKey();
            int loc = e.getValue();
            out_map.put(t, lines.get(t).get(loc));
        }

        return out_map;
    }

    public Station moveTrain(Train t) {
        
        int loc = t_locations.get(t);
        int mov = t_movements.get(t);

        loc += mov;

        if (loc >= lines.get(t).size()) {
            loc -= 2;
            t_movements.put(t, mov * -1);
        } else if (loc < 0) {
            loc += 2;
            t_movements.put(t, mov * -1);
        }

        t_locations.put(t, loc);
        return lines.get(t).get(loc);
    }

    public Station passengerNextStation(Passenger p) {
        int loc = p_locations.get(p);

        if (loc == -1) {
            return null;
        } else {
            return journeys.get(p).get(loc + 1);
        }
    }

    public Train passengerTrain(Passenger p) {
        for (Map.Entry<Train, Map<Station, List<Passenger>>> e : trains.entrySet()) {
            Train t = e.getKey();
            for (Map.Entry<Station, List<Passenger>> car : e.getValue().entrySet()) {
                if (car.getValue().contains(p)) {
                    return t;
                }
            }
        }
        return null;
    }

    public Station passengerStation(Passenger p) {
        int loc = p_locations.get(p);

        if (loc == -1) {
            return null;
        } else if (passengerTrain(p) != null) {
            return null;
        } else {
            return journeys.get(p).get(loc);
        }
    }

    public void boardTrain(Passenger p, Train t, Station s) {
        trains.get(t).get(passengerNextStation(p)).add(p);
        stations.get(s).remove(p);
    }

    public void deboardTrain(Passenger p, Train t, Station s) {
        trains.get(t).get(s).remove(p);
        
        int loc = p_locations.get(p) + 1;
        if (loc == journeys.get(p).size() - 1) {
            loc = -1;
        } else {            
            stations.get(s).add(p);
        }

        p_locations.put(p, loc);
    }
}
