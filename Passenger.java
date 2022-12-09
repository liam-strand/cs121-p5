/* Passenger.java
 * 
 * By: Liam Strand
 * On: December 2022
 * 
 * A typed, singleton string representing a passenger's name.
 */

import java.util.HashMap;

public class Passenger extends Entity {

    private static HashMap<String, Passenger> passengers = new HashMap<>();

    private Passenger(String name) { super(name); }

    public static Passenger make(String name) {
        Passenger p = passengers.get(name);

        if (p == null) {
            p = new Passenger(name);
            passengers.put(name, p);
        }

        return p;
    }
}
