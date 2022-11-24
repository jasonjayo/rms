package com.rms.restaurantmanagementsystemgui;

import java.util.ArrayList;

public class Menu {

    ArrayList<MenuCategory> menu = new ArrayList<>();

    public Menu() {

    }


//    public Menu(TreeMap<String, MenuCategory> menu) {
//        this.menu = menu;
//    }

    public ArrayList<MenuCategory> getCategories() {
        return menu;
    }

    public ArrayList<FoodItem> getAllItems() {
        ArrayList<FoodItem> allItems = new ArrayList<>();

        for (MenuCategory mc : menu) {
            allItems.addAll(mc.getFood());
        }
        return allItems;
    }

}