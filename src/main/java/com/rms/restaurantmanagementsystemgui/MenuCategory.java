package com.rms.restaurantmanagementsystemgui;

import java.util.ArrayList;

/**
 * A menu category holds food items and belongs to a menu
 *
 * @author Adam Greenan
 */
public class MenuCategory {

    private final String name;
    private final ArrayList<FoodItem> food = new ArrayList<>();

    /**
     * create a new menu category
     *
     * @param name String name of category
     */
    public MenuCategory(String name) {
        this.name = name;
    }

    /**
     * returns name of category
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * adds food item to menu category
     *
     * @param f Food Item
     */
    public void addFoodItem(FoodItem f) {
        food.add(f);
    }

    /**
     * gets list of food in category
     *
     * @return array list of food items
     */
    public ArrayList<FoodItem> getFood() {
        return food;
    }

}
