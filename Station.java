import java.util.HashMap;

public class Station extends Entity {

    private static HashMap<String, Station> stations = new HashMap<>();

    private Station(String name) { super(name); }

    public static Station make(String name) {
        Station s = stations.get(name);

        if (s == null) {
            s = new Station(name);
            stations.put(name, s);
        }

        return s;
    }
}
