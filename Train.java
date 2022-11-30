import java.util.HashMap;

public class Train extends Entity {

    private static HashMap<String, Train> trains = new HashMap<>();

    private Train(String name) { super(name); }

    public static Train make(String name) {
        Train t = trains.get(name);

        if (t == null) {
            t = new Train(name);
            trains.put(name, t);
        }

        return t;
    }
}
