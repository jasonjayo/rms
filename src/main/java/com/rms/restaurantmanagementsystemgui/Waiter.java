package com.rms.restaurantmanagementsystemgui;

import java.util.ArrayList;


public class Waiter extends Staff {

    public Waiter(int id, String name, String password, int restaurantId) {
        super(id, name, password, restaurantId);
    }

    private int tableID;
    private int customerID; /*dunno if this is needed */
    private String status;
    private ArrayList<FoodItem> Order;

    public void newOrderFood(FoodItem food) {
        Order.add(food);
    }

    public void removeFood(FoodItem food) {
        Order.remove(food);
    }

    public void newOrder(int tableID, int customerID) {
        this.tableID = tableID;
        this.customerID = customerID;

    }

    public void setStatus(String status) {
        this.status = status;
    }
}

