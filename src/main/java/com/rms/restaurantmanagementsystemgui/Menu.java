package com.rms.restaurantmanagementsystemgui;

import java.util.ArrayList;

/**
 * Menu of a restaurant. Has various menu categories which each contain food.
 *
 * @author Adam Greenan
 */
public class Menu {

    private final ArrayList<MenuCategory> menu = new ArrayList<>();

    /**
     * returns list of all menu categories
     *
     * @return array list of menu categories
     */
    public ArrayList<MenuCategory> getCategories() {
        return menu;
    }

    /**
     * returns all items on menu incl. all categories
     *
     * @return array list of food items
     */
    public ArrayList<FoodItem> getAllItems() {
        ArrayList<FoodItem> allItems = new ArrayList<>();

        for (MenuCategory mc : menu) {
            allItems.addAll(mc.getFood());
        }
        return allItems;
    }

}