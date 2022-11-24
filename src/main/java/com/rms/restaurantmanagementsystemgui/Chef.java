package com.rms.restaurantmanagementsystemgui;

public class Chef extends Staff {
    public void chefSettingStatus(Order o) {
//        o.setOrderStatus(2);
    }

    public Chef(int id, String name, String password, int restaurantId) {
        super(id, name, password, restaurantId);
    }

}
