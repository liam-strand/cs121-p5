/* Car.java
 * 
 * By: Liam Strand
 * On: December 2022
 * 
 * A wait/notify target for passengers waiting to get off a train.
 */

public class Car {
    Train t;
    Station s;

    public Car(Train t, Station s) {
        this.t = t;
        this.s = s;
    }

    public Station getStation() {
        return s;
    }
}
