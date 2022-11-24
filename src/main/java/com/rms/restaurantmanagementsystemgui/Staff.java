package com.rms.restaurantmanagementsystemgui;

public class Staff {

    private final int id;
    private final String name;
    private final String password;
    private final int restaurantId;

    public Staff(int id, String name, String password, int restaurantId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.restaurantId = restaurantId;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", restaurantId=" + restaurantId +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
