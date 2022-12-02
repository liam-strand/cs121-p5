import java.util.*;

public class Stop {
    private Station s;
    private Set<Journey> w;

    public Stop(Station s) {
        this.s = s;
        w = new HashSet<>();
    }

    public void add(Journey j) {
        w.add(j);
    }

    public Journey[] dump() {
        Journey[] journies = w.toArray(Journey[]::new);
        w.clear();
        return journies;
    }

    public Station getStation() {
        return s;
    }

    public boolean contains(Passenger p) {
        for (Journey j : w.toArray(Journey[]::new)) {
            if (j.getPassenger() == p) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return String.format("%s: %s", s, w);
    }
}
