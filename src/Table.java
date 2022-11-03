public class Table {
    private int id;
    private int capacity;

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
