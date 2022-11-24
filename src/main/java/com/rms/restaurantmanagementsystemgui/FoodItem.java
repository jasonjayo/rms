package com.rms.restaurantmanagementsystemgui;

public class FoodItem {

    private String name;
    private double price;

    private int id;

    public FoodItem(String name, double price, int id) {
        this.price = price;
        this.name = name;
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("%-20s EUR %.2f", name, price);
    }
}
