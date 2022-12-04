import java.util.concurrent.locks.ReentrantLock;
import java.util.*;

public class Line extends Thread {
    private Train t;
    private Map<Station, Car> cars;
    private List<Station> stops;
    private int idx;
    private int dir;
    private int len;
    private final MBTA mbta;
    private final Log log;
    private final Actors actors;
    private Map<Station, Map<Train, Platform>> platforms;

    public Line(Train t, List<Station> stations, MBTA mbta, Log log, Map<Station, Map<Train, Platform>> platforms, Actors actors) {
        this.t = t;
        this.stops = stations;
        this.cars = new HashMap<>();
        stations.forEach(s -> cars.put(s, new Car(t, s)));

        this.dir = 1;
        this.idx = 0;
        this.len = stations.size();

        this.mbta = mbta;
        this.log = log;
        this.platforms = platforms;
        this.actors = actors;
    }

    @Override
    public void run() {
        Station curr = stops.get(idx);
        while (!Thread.interrupted()) {
            try {
                // Tell everyone who needs to get off to deboard the train
                Car c = cars.get(curr);
                synchronized(c) { c.notifyAll(); }
                
                // Tell everyone who needs to get on to board the train
                Platform p = platforms.get(curr).get(t);
                synchronized(p) { p.notifyAll(); }

                Thread.sleep(500);

                // move the train
                Station old = stops.get(idx);
                advance();
                curr = stops.get(idx);
                synchronized(curr) {
                    while(!mbta.stationHasNoTrain(curr)) {
                        curr.wait();
                    }
                }
                mbta.moveTrain(t, old, curr);
                log.train_moves(t, old, curr);
                synchronized(old) { old.notifyAll(); }
            
            } catch (InterruptedException ex) {
                this.interrupt();
            }
        }
    }

    private void advance() {
        idx += dir;

        if (idx >= len) {
            dir *= -1;
            idx = len - 2;
        } else if (idx < 0) {
            dir *= -1;
            idx = 1;
        }
    }

    public Car getCar(Station s) {
        return cars.get(s);
    }
}
