import java.util.concurrent.*;
import java.util.*;
import java.util.stream.*;

public class Journey extends Thread {
    private Passenger p;
    private Queue<Station> path;
    private boolean on_train;
    private Station firstStation;
    private Station currentStation;
    private final MBTA mbta;
    private final Log log;
    private final Actors actors;
    private final Map<Station, Map<Train, Platform>> platforms;

    public Journey(Passenger p, List<Station> path, MBTA mbta, Log log, Map<Station, Map<Train, Platform>> platforms, Actors actors) {
        this.p = p;
        this.path = new LinkedList<>(path);
        this.firstStation = this.path.remove();
        this.currentStation = this.firstStation;
        this.mbta = mbta;
        this.log = log;
        this.platforms = platforms;
        this.actors = actors;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted() && !path.isEmpty()) {
                // Find what platform to wait at
                Platform plat = findPlatform(currentStation, path.peek());
                Train t = plat.getTrain();

                // Wait on the platform
                synchronized(plat) { plat.wait(); }

                // Board the train when it arrives
                mbta.boardTrain(p, t, currentStation);
                log.passenger_boards(p, t, currentStation);

                // Find what car to wait in
                Car c = actors.getLine(t).getCar(path.peek());
                
                // wait in that car
                synchronized(c) { c.wait(); }

                // deboard the car
                currentStation = path.poll();
                mbta.deboardTrain(p, t, currentStation);
                log.passenger_deboards(p, t, currentStation);
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException("Journey interrupted");
        }
    }

    private Platform findPlatform(Station s1, Station s2) {
        Collection<Platform> s1_platforms = platforms.get(s1).values();
        Collection<Platform> s2_platforms = platforms.get(s2).values();

        // System.err.println(s1_platforms);
        // System.err.println(s2_platforms);

        for (Platform p1 : s1_platforms) {
            Train t = p1.getTrain();
            for (Platform p2 : s2_platforms) {
                if (t == p2.getTrain()) {
                    return p1;
                }
            }
        }

        return null;
    }

    public String toString() {
        return String.format("%s: %s", p, path);
    }
}
