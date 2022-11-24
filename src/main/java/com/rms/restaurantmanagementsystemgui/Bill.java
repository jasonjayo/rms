package com.rms.restaurantmanagementsystemgui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class Bill {

    private final ArrayList<FoodItem> items;
    private double tip;

    private boolean paid;

    public Bill(ArrayList<FoodItem> items) {
        this.items = items;
    }

    public double getBillTotal() {
        double billTotal = 0;
        //getting total amount to pay from all food items in Order
        for (FoodItem food : items) {
            billTotal += food.getPrice();
        }
        billTotal += tip;
        return billTotal;
    }

    public void setTip(double aTip) {
        this.tip = aTip;
    }

    public double getTip() {
        return tip;
    }

    public String toString() { //printing the bill method?
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
