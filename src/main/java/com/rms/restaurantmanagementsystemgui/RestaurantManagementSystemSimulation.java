package com.rms.restaurantmanagementsystemgui;

import javafx.application.Application;

public class RestaurantManagementSystemSimulation {
    public static void main(String[] args) {

        RestaurantManagementSystem rms = new RestaurantManagementSystem();

        if (args.length > 0 && args[0].equals("-gui")) {
            Application.launch(RestaurantManagementSystemGUI.class, args);
        } else {
            RestaurantManagementSystemMenu cliMenu = new RestaurantManagementSystemMenu();
            cliMenu.run(rms);
        }
    }
}
