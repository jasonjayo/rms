package com.rms.restaurantmanagementsystemgui;

/**
 * A table in a restaurant
 *
 * @author Caragh Morahan
 */
public class Table {
    private final int id;
    private final int capacity;

    /**
     * creates a table
     *
     * @param id       int table id
     * @param capacity int capacity of table
     */
    public Table(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    /**
     * returns id of table
     *
     * @return int table id
     */
    public int getId() {
        return id;
    }

    /**
     * returns capacity of table
     *
     * @return int capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * returns human-readable summary of table
     *
     * @return String
     */
    public String toString() {
        return String.format("Table %d (capacity: %d)", id, capacity);
    }

}
