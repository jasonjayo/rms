module com.rms.restaurantmanagementsystemgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.rms.restaurantmanagementsystemgui to javafx.fxml;
    exports com.rms.restaurantmanagementsystemgui;
}