package com.rms.restaurantmanagementsystemgui;

/**
 * A food item represents each purchasable item from the menu.
 * Food items are in menu categories
 *
 * @author Adam Greenan
 */
public class FoodItem {

    private final String name;
    private final double price;
    private final int id;

    /**
     * constructs a new food item given the info
     *
     * @param name  name of food item
     * @param price price in euro (e.g., 3.50)
     * @param id    unique id
     */
    public FoodItem(String name, double price, int id) {
        this.price = price;
        this.name = name;
        this.id = id;
    }

    /**
     * returns price of item
     *
     * @return double price
     */
    public double getPrice() {
        return price;
    }

    /**
     * returns name of item
     *
     * @return string name
     */
    public String getName() {
        return name;
    }

    /**
     * returns id of item
     *
     * @return int id
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%-20s EUR %.2f", name, price);
    }
}
