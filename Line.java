import java.util.concurrent.*;
import java.util.*;

public class Line extends Thread {
    private Train t;
    private List<Stop> stops;
    private int train_idx;
    private int train_dir;

    public Line(Train t, List<Station> stations) {
        this.t = t;
        this.stops = new ArrayList<>();
        stations.forEach(s -> stops.add(new Stop(s)));
        
        this.train_dir = 1;
        this.train_idx = 0;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    synchronized public Station findPassenger(Passenger p) {
        for (Stop s : stops) {
            if (s.contains(p)) {
                return s.getStation();
            }
        }
        return null;
    }

    public void addJourney(Journey j) {
        Station target = j.next();
        for (Stop stop : stops) {
            if (target == stop.getStation()) {
                stop.add(j);
                return;
            }
        }
        throw new RuntimeException("Passenger tried to board the wrong train");
    }

    public Train getTrain() {
        return t;
    }

    public Station find() {
        return stops.get(train_idx).getStation();
    }

    public Stop advance() {
        train_idx += train_dir;

        if (train_idx < 0) {
            train_idx += 2;
            train_dir *= -1;
        } else if (train_idx >= stops.size()) {
            train_idx -= 2;
            train_dir *= -1;
        }

        return stops.get(train_idx);
    }

    public boolean isAtStart() {
        return train_idx == 0;
    }

    public String toString() {
        return String.format("%s%s(%s): [%s]", t, train_dir == 1 ? ">" : "<", train_idx, stops.stream().map(e -> String.format("%s |", e)));
    }
}
