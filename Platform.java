/* Platform.java
 * 
 * By: Liam Strand
 * On: December 2022
 * 
 * Acts as a wait/notify target for passengers attempting to board trains.
 */

public class Platform {
    private final Train t;
    private final Station s;

    public Platform(Train t, Station s) {
        this.t = t;
        this.s = s;
    }

    public Train getTrain() {
        return t;
    }

    public String toString() {
        return String.format("%s: %s", s, t);
    }
}
