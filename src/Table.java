import java.util.ArrayList;

public class Table {
    private int id;
    private int capacity;

    private ArrayList<Reservation> reservations;

    public Table(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public String toString() {
        return String.format("[Table ID: %d, capacity: %d]", id, capacity);
    }
}
