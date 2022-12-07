import java.util.*;
import java.util.Map.Entry;

public class Actors {

    private Map<Passenger, Journey> journies = new HashMap<>();
    private Map<Train,     Line>    lines    = new HashMap<>();
    private Map<Station, Map<Train, Platform>> platforms = new HashMap<>();
        
    public Actors(Map<Passenger, List<Station>> journeySpecs, Map<Train, List<Station>> lineSpecs, MBTA mbta, Log log, int wait_time) {
        for (Entry<Passenger, List<Station>> journeySpec : journeySpecs.entrySet()) {
            journies.put(journeySpec.getKey(), new Journey(journeySpec.getKey(), journeySpec.getValue(), mbta, log, platforms, this));
        }

        for (Entry<Train, List<Station>> lineSpec : lineSpecs.entrySet()) {
            Train t = lineSpec.getKey();
            List<Station> stations = lineSpec.getValue();
            lines.put(lineSpec.getKey(), new Line(t, stations, mbta, log, platforms, wait_time));

            for (Station s : stations) {
                if (platforms.get(s) == null) {
                    platforms.put(s, new HashMap<>());
                }

                platforms.get(s).put(t, new Platform(t, s));
            }
        }
    }

    public Actors(Map<Passenger, List<Station>> journeySpecs, Map<Train, List<Station>> lineSpecs, MBTA mbta, Log log) { 
        this(journeySpecs, lineSpecs, mbta, log, 500);
    }

    public void run() {
        journies.values().forEach(j -> j.start());
        lines.values().forEach(l -> l.start());
        journies.values().forEach(j -> uncheckedJoin(j));
        // System.err.println("INTERRUPTING LINES");
        // lines.values().forEach(l -> l.interrupt());
        // lines.values().forEach(l -> uncheckedJoin(l));
    }

    private void uncheckedJoin(Thread t) {
        try {
            t.join();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Something went horribly wrong", ex);
        }
    }

    public Line getLine(Train t) {
        return lines.get(t);
    }
}
