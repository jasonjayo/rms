package com.rms.restaurantmanagementsystemgui;

/**
 * class to store details about staff.
 *
 * @author Kamil Mrowiec
 */
public class Staff {

    private final int id;
    private final String name;
    private final String password;
    private final int restaurantId;

    /**
     * creates new staff member
     *
     * @param id           int it
     * @param name         String name
     * @param password     String password
     * @param restaurantId int restaurant id of restaurant this staff members works for
     */
    public Staff(int id, String name, String password, int restaurantId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.restaurantId = restaurantId;
    }

    /**
     * returns name of staff member
     *
     * @return String full name
     */
    public String getName() {
        return name;
    }

    /**
     * returns password of staff member
     *
     * @return String password
     */
    public String getPassword() {
        return password;
    }
}
