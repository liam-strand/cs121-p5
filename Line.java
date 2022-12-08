import java.util.logging.*;
import java.util.*;


public class Line extends Thread {
    private Train t;
    private Map<Station, Car> cars;
    private List<Station> stops;
    private int idx;
    private int dir;
    private int len;
    private static MBTA mbta;
    private static Log log;
    private static Map<Station, Map<Train, Platform>> platforms;
    private int count = 0;

    private static int wait_time;

    private static Logger logger = Logger.getLogger(Line.class.getName());

    public Line(Train t, List<Station> stations, MBTA mbta, Log log, Map<Station, Map<Train, Platform>> platforms, int wait_time) {
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

        this.wait_time = wait_time;

        logger.setLevel(Level.OFF);
        Arrays.stream(logger.getHandlers()).forEach( h -> h.setLevel(logger.getLevel()));
    }

    public Line(Train t, List<Station> stations, MBTA mbta, Log log, Map<Station, Map<Train, Platform>> platforms) {
        this(t, stations, mbta, log, platforms, 500);
    }

    @Override
    public void run() {
        Station curr = stops.get(idx);
        curr.lock(t);
        while (!this.isInterrupted()) {
            try {
                // find the next station
                Station old = curr;
                advance();
                curr = stops.get(idx);
                
                logger.warning(String.format("%s at thread %d it %d", t, this.getId(), count++));

                // wait until station is available
                logger.warning(String.format("%s waiting for %s to open\n", t, curr));
                curr.lock(t);

                if (this.isInterrupted()) {
                    logger.finer(String.format("%s INTERRUPTED WITH LOCK\n", t, curr));
                    this.interrupt();
                    old.unlock(t);
                    curr.unlock(t);
                    return;
                }

                logger.fine(String.format("%s at %s\n", t, curr));
                // move the train to the new station
                log.train_moves(t, old, curr);
                mbta.moveTrain(t, old, curr);
                
                // release the lock on the old station
                logger.finer(String.format("%s allowing other trains to enter %s\n", t, old));
                old.unlock(t);
    
                // Tell everyone who needs to get off to deboard the train
                logger.finer(String.format("%s deboarding at %s\n", t, curr));
                Car c = cars.get(curr);
                synchronized(c) { c.notifyAll(); }
                
                // Tell everyone who needs to get on to board the train
                logger.finer(String.format("%s boarding at %s\n", t, curr));
                Platform p = platforms.get(curr).get(t);
                synchronized(p) { p.notifyAll(); }
    
                // wait at the station
                logger.finer(String.format("%s waiting in %s\n", t, curr));
                Thread.sleep(wait_time);
                
                // System.err.printf("%s leaving %s\n", t, curr);
            } catch (InterruptedException ex) {
                // System.err.printf("%s CAUGHT INTERRUPTION\n", t);
                this.interrupt();
                curr.unlock(t);
                return;
            }
        }
        // System.err.printf("%s INTERUPTED OUT Of LOOP\n", t);
        curr.unlock(t);
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

    public Train getTrain() {
        return t;
    }
}
