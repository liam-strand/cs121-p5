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
