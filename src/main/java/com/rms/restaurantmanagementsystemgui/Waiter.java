package com.rms.restaurantmanagementsystemgui;


/**
 * holds
 * A waiter who works in one of the chain's restaurants
 *
 * @author Kamil Mrowiec
 */

public class Waiter extends Staff {

    /**
     * creates a waiter given their details
     *
     * @param id           int id
     * @param name         String name
     * @param password     String password
     * @param restaurantId int restaurant id indicating which restaurant in chain waiter works for
     */
    public Waiter(int id, String name, String password, int restaurantId) {
        super(id, name, password, restaurantId);
    }

}


