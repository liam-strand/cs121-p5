import java.util.*;
import java.util.logging.*;
import java.util.Map.Entry;
import java.io.File;

public class MBTA {
    
    /* STATE */
    private Map<Station, List<Passenger>> station_passengers;
    private Map<Train,   Station>         train_locations;
    private Map<Train,   List<Passenger>> train_passengers;
    private Map<Passenger, List<Station>> stations_remaining;

    /* CONFIGURATION */
    private Map<Passenger, List<Station>> journies;
    private Map<Train,     List<Station>> lines;

    /* UTILITY */
    // private static final Logger MY_LOG = Logger.getLogger("MBTA");

    // Creates an initially empty simulation
    public MBTA() { 
        reset();
    }

    // Adds a new transit line with given name and stations
    public void addLine(String name, List<String> stations) {
        Train t = Train.make(name);
        
        lines.put(t, new ArrayList<>());
        stations.forEach(str -> {
            Station s = Station.make(str);
            station_passengers.put(s, new ArrayList<>());
            lines.get(t).add(s);
        });
        
        train_locations.put(t, lines.get(t).get(0));
        train_passengers.put(t, new ArrayList<>());
    }

    // Adds a new planned journey to the simulation
    public void addJourney(String name, List<String> stations) {
        Passenger p = Passenger.make(name);

        journies.put(p, stations.stream().map(str -> Station.make(str)).toList());
        
        stations_remaining.put(p, new LinkedList<>());
        journies.get(p).forEach(s -> stations_remaining.get(p).add(s));

        station_passengers.get(stations_remaining.get(p).get(0)).add(p);
        stations_remaining.get(p).remove(0);
    }

    // Return normally if initial simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkStart() {
        for (Entry<Passenger, List<Station>> journey : journies.entrySet()) {
            Passenger p = journey.getKey();
            Station start = journey.getValue().get(0);

            if (!station_passengers.get(start).contains(p)) {
                throw new RuntimeException(String.format("Passenger %s was not at %s", p, start));
            }
        }

        for (Entry<Train, List<Station>> line : lines.entrySet()) {
            Train t = line.getKey();
            Station start = line.getValue().get(0);

            if (train_locations.get(t) != start) {
                throw new RuntimeException(String.format("Train %s was not at %s", t, start));
            }
        }
    }

    // Return normally if final simulation conditions are satisfied, otherwise
    // raises an exception
    public void checkEnd() {
        for (Entry<Passenger, List<Station>> journey : stations_remaining.entrySet()) {
            if (!journey.getValue().isEmpty()) {
                throw new RuntimeException(String.format("Passenger %s did not complete their journey", journey.getKey()));
            }
        }
    }

    // reset to an empty simulation
    public void reset() {
        station_passengers = Collections.synchronizedMap(new HashMap<>());
        train_locations    = Collections.synchronizedMap(new HashMap<>());
        train_passengers   = Collections.synchronizedMap(new HashMap<>());
        stations_remaining = Collections.synchronizedMap(new HashMap<>());
        
        journies = new HashMap<>();
        lines    = new HashMap<>();
    }

    // adds simulation configuration from a file
    public void loadConfig(String filename) {
        Config config = Config.fromFile(new File(filename));

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

    public void moveTrain(Train t, Station s1, Station s2) {
        train_locations.put(t, s2);
    }

    public void boardTrain(Passenger p, Train t, Station s) {
        station_passengers.get(s).remove(p);
        train_passengers.get(t).add(p);
    }

    public void deboardTrain(Passenger p, Train t, Station s) {
        train_passengers.get(t).remove(p);
        station_passengers.get(s).add(p);
        stations_remaining.get(p).remove(0);
    }

    public void moveTrainChecked(Train t, Station s1, Station s2) {

        Station start = train_locations.get(t);

        if (start != s1) {
            throw new RuntimeException("Train started at the wrong station");
        }

        synchronized(train_locations) {
            for (Station loc : train_locations.values()) {
                if (loc == s2) {
                    throw new RuntimeException("Train entered a station where a train already was");
                }
            }
        }

        moveTrain(t, s1, s2);

        Station end = train_locations.get(t);
        if (end != s2) {
            throw new RuntimeException("Train ended at the wrong station");
        }
    }

    public void boardTrainChecked(Passenger p, Train t, Station s) {

        if (s != train_locations.get(t)) {
            throw new RuntimeException(String.format("The train (%s) was not at the expected station (expected %s got %s)", t, s, train_locations.get(t)));
        }

        if (!stationHasNoTrain(s)) 

        if (s != findPassengerStation(p)) {
            throw new RuntimeException(String.format("The passenger (%s) was not at the expected station (expected %s got %s)", p, s, findPassengerStation(p)));
        }

        boardTrain(p, t, s);
    }

    public void deboardTrainChecked(Passenger p, Train t, Station s) {

        if (s != train_locations.get(t)) {
            throw new RuntimeException(String.format("The train (%s) was not at the expected station (expected %s got %s)", t, s, train_locations.get(t)));
        }

        if (s != passengerNextStation(p)) {
            throw new RuntimeException(String.format("The passenger (%s) was not supposed to go to the current station (wanted %s got %s)", p, passengerNextStation(p), s));
        }

        deboardTrain(p, t, s);
    }

    private Station findPassengerStation(Passenger p) {
        synchronized(station_passengers) {
            for (Entry<Station, List<Passenger>> station : station_passengers.entrySet()) {
                Station s = station.getKey();
                if(station.getValue().contains(p)) {
                    return s;
                }
            }
            return null;
        }
    }

    private Station passengerNextStation(Passenger p) {
        synchronized(stations_remaining) {
            return stations_remaining.get(p).get(0);
        }
    }

    private Train findPassengerTrain(Passenger p) {
        synchronized(train_passengers) {
            for (Entry<Train, List<Passenger>> train : train_passengers.entrySet()) {
                Train t = train.getKey();
                if(train.getValue().contains(p)) {
                    return t;
                }
            }
            return null;
        }
    }

    public boolean stationHasNoTrain(Station s) {
        synchronized(train_locations) {
            return !train_locations.values().contains(s);
        }
    }

    public Station findTrain(Train t) {
        return train_locations.get(t);
    }

    public void run(Log l) {
        Actors actors = new Actors(journies, lines, this, l);
        actors.run();
    }
}
