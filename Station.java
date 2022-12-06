import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Station extends Entity {

    private static HashMap<String, Station> stations = new HashMap<>();

    private Station(String name) { super(name); }

    private ReentrantLock lock = new ReentrantLock(true);

    public static Station make(String name) {
        Station s = stations.get(name);

        if (s == null) {
            s = new Station(name);
            stations.put(name, s);
        }

        return s;
    }

    public void lock(Train t) {
        lock.lock();
        System.err.printf("%s locked %s\n", t, this);
    }

    public void unlock(Train t) {
        System.err.printf("%s unlocking %s\n", t, this);
        lock.unlock();
    }
}
