package com.rms.restaurantmanagementsystemgui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FxFXMLController {

    RestaurantManagementSystem rms = new RestaurantManagementSystem();
    private Restaurant currentRestaurant = rms.getRestaurants().get(0);

    @FXML
    private DatePicker cr_datePicker;
    @FXML
    private ChoiceBox<String> cr_timePicker;
    @FXML
    private ChoiceBox<Table> cr_pickTable;
    @FXML
    private TextField cr_numPeople;
    @FXML
    private TextField cr_customerId;
    @FXML
    private TextField cr_phoneNum;

    @FXML
    private FlowPane cr_phoneNumContainer;

    @FXML
    private Text cr_message;

    @FXML
    void initialize() {
        cr_phoneNumContainer.setVisible(false);
    }

    public void getTimes() {
        List<String> times = currentRestaurant.getAvailableStartTimes();
        cr_timePicker.getItems().clear();
        cr_timePicker.getItems().addAll(times);
        cr_timePicker.setValue(cr_timePicker.getItems().get(0));
    }

    public void getTables() {
        cr_pickTable.getItems().clear();
        cr_pickTable.getItems().addAll(currentRestaurant.getAvailableTables(cr_datePicker.getValue().atTime(LocalTime.parse(cr_timePicker.getValue())), Integer.parseInt(cr_numPeople.getText())));
        cr_pickTable.setValue(cr_pickTable.getItems().get(0));
    }

    public void getCustomer() {
        cr_phoneNumContainer.setVisible(!cr_customerId.getText().chars().allMatch(Character::isDigit));
    }

    public void createReservation() {
        int customerId;
        String feedback = "";
        if (cr_customerId.getText().chars().allMatch(Character::isDigit)) {
            customerId = Integer.parseInt(cr_customerId.getText());
        } else {
            String customerName = cr_customerId.getText();
            String phoneNum = cr_phoneNum.getText();
            customerId = currentRestaurant.createCustomer(customerName, phoneNum);
            feedback += "We've created a new account for you. Keep a record of your customer ID: " + customerId + "\n";
        }
        feedback += "Here's a summary of your reservation: \n" + currentRestaurant.createReservation(cr_pickTable.getValue(), Integer.parseInt(cr_numPeople.getText()), customerId, cr_datePicker.getValue().atTime(LocalTime.parse(cr_timePicker.getValue())));
        cr_message.setText(feedback);
    }
}
