package com.rms.restaurantmanagementsystemgui;

/**
 * A customer who has made an order
 *
 * @author Kamil Mrowiec
 */

public class Customer {

    private final int id;

    private String name;

    private String phoneNum;

    /**
     * create customer given details
     *
     * @param id       int customer id
     * @param name     String first and last name
     * @param phoneNum int phone number
     */

    public Customer(int id, String name, String phoneNum) {
        this.id = id;
        this.name = name;
        this.phoneNum = phoneNum;
    }

    /**
     * returns customer id
     *
     * @return int customer id
     */
    public int getId() {
        return id;
    }

    /**
     * returns customer name
     *
     * @return String customer name
     */
    public String getName() {
        return name;
    }

    /**
     * returns customer phone number
     *
     * @return String customer phone number
     */
    public String getPhoneNum() {
        return phoneNum;
    }

    /**
     * sets customer name to given name
     *
     * @param name String new customer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets customer phone number to give phone number
     *
     * @param phoneNum String new phone number
     */
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

}
