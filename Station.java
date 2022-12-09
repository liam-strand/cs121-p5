/* Station.java
 * 
 * By: Liam Strand
 * On: December 2022
 * 
 * A typed, lockable, singleton string that represents a station.
 */

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Station extends Entity {

    private static HashMap<String, Station> stations = new HashMap<>();

    private Station(String name) { super(name); }

    private ReentrantLock lock = new ReentrantLock();

    private static Logger logger = Logger.getLogger("metroSim");

    public static Station make(String name) {
        Station s = stations.get(name);

        if (s == null) {
            s = new Station(name);
            stations.put(name, s);
        }

        return s;
    }

    public void lock() {
        lock.lock();
        logger.finest(() -> String.format("%s locking\n", this));
    }

    public void unlock() {
        logger.finest(() -> String.format("%s unlocking\n", this));
        lock.unlock();
    }
}
