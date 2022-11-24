package com.rms.restaurantmanagementsystemgui;

/**
 * A chef who works in one of the chain's restaurants
 *
 * @author Kamil Mrowiec
 */
public class Chef extends Staff {
    /**
     * create a new chef given the details
     *
     * @param id           chef's id
     * @param name         first and last name
     * @param password     password
     * @param restaurantId restaurant id
     */
    public Chef(int id, String name, String password, int restaurantId) {
        super(id, name, password, restaurantId);
    }
}
