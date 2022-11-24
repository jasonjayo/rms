package com.rms.restaurantmanagementsystemgui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

/**
 * Bill holds information about total cost of items in an order as well as the tip
 * Also provides printed summary of these details.
 *
 * @author Caragh Morahan
 */
public class Bill {

    private final ArrayList<FoodItem> items;
    private double tip;

    private boolean paid;

    /**
     * creates a bill with the given food items
     *
     * @param items array list of food items
     */
    public Bill(ArrayList<FoodItem> items) {
        this.items = items;
    }

    /**
     * returns bill total
     *
     * @return double bill total
     */
    public double getBillTotal() {
        double billTotal = 0;
        //getting total amount to pay from all food items in Order
        for (FoodItem food : items) {
            billTotal += food.getPrice();
        }
        billTotal += tip;
        return billTotal;
    }

    /**
     * set the tip to given value
     *
     * @param aTip double value to set tip to
     */
    public void setTip(double aTip) {
        this.tip = aTip;
    }

    /**
     * gets tip
     *
     * @return double tip value
     */
    public double getTip() {
        return tip;
    }

    /**
     * returns full bill info as string including list of items, tip and total price
     *
     * @return string
     */
    public String toString() {
        //print Bill total and tip as amount due to pay
        String nameAndPrice = "";
        for (FoodItem food : items) {
            nameAndPrice += food + "\n";
        }
        String bill = String.format("%-20s %s%.2f\n", "Total bill is ", "EUR ", getBillTotal());
        String tip = String.format("%-20s %s%.2f\n", "Added tip is ", "EUR ", getTip());
        return LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + "\n"
                + "=".repeat(30) + "\n"
                + nameAndPrice
                + "=".repeat(30) + "\n"
                + tip
                + bill
                + "=".repeat(30);
    }
}
