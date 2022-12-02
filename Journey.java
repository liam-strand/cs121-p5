import java.util.concurrent.*;
import java.util.*;

public class Journey extends Thread {
    private Passenger p;
    private Queue<Station> path;
    private boolean on_train;
    private Station firstStation;

    public Journey(Passenger p, List<Station> path) {
        this.p = p;
        this.path = new LinkedList<>(path);
        this.on_train = false;
        this.firstStation = path.get(0);
        this.path.remove();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    public Station next() {
        return path.peek();
    }

    public boolean isDone() {
        return path.isEmpty();
    }

    public Station first() {
        return firstStation;
    }

    synchronized public Passenger getPassenger() {
        return p;
    }

    public void board() {
        on_train = true;
    }

    public void deboard() {
        on_train = false;
        path.remove();
    }

    public String toString() {
        return String.format("%s: %s", p, path);
    }
}
