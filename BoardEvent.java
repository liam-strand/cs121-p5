import java.util.*;

public class BoardEvent implements Event {
    public final Passenger p; public final Train t; public final Station s;
    public BoardEvent(Passenger p, Train t, Station s) {
        this.p = p; this.t = t; this.s = s;
    }
    public boolean equals(Object o) {
        if (o instanceof BoardEvent e) {
            return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
        }
        return false;
    }
    public int hashCode() {
        return Objects.hash(p, t, s);
    }
    public String toString() {
        return "Passenger " + p + " boards " + t + " at " + s;
    }
    public List<String> toStringList() {
        return List.of(p.toString(), t.toString(), s.toString());
    }
    public void replayAndCheck(MBTA mbta) {
        Map<Train, Station> train_stat = mbta.allTrainLocs();
        
        if (s != train_stat.get(t)) {
            throw new RuntimeException(String.format("The train (%s) was not at the expected station (expected %s got %s)", t, s, train_stat.get(t)));
        }

        if (s != mbta.findPassengerInStation(p)) {
            throw new RuntimeException(String.format("The passenger (%s) was not at the expected station (expected %s got %s)", p, s, mbta.findPassengerInStation(p)));
        }

        mbta.boardTrain(p, t, s);
    }
}
