package com.rms.restaurantmanagementsystemgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class RestaurantManagementSystemGUI extends Application {

    Restaurant currentRestaurant;
    LocalDateTime dateTime;
    Table chosenTable;
    Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));

        BorderPane bp = new BorderPane();
        HBox mainButtons = new HBox(10);

        mainButtons.setStyle("-fx-padding: 10px; -fx-background-color: #e3dbd7");

        Button createReservationButton = new Button("Make a reservation");
        Button cancelReservationButton = new Button("Cancel reservation");
        Button walkInBookingButton = new Button("Walk-in booking");
        Button viewMenuButton = new Button("View menu");
        Button orderButton = new Button("Order");
        Button requestBillButton = new Button("Request bill");
        Button selectRestaurantButton = new Button("Select another restaurant");
        Button payBillButton = new Button("Pay bill");
        Button incomeSummaryButton = new Button("Income summary");
        Button quitButton = new Button("Quit");

        mainButtons.getChildren().addAll(createReservationButton, cancelReservationButton, walkInBookingButton, viewMenuButton, orderButton, requestBillButton, selectRestaurantButton, payBillButton, incomeSummaryButton, quitButton);

        bp.setTop(mainButtons);

        // create reservation
        FlowPane createReservation = new FlowPane();
        createReservation.setOrientation(Orientation.VERTICAL);
        createReservation.setAlignment(Pos.CENTER);
        createReservation.setStyle("-fx-padding: 20px");
        Label datePickerLabel = new Label("Please pick a date:");

        Label numOfPeopleLabel = new Label("How many people are dining?");
        TextField numOfPeople = new TextField();

        DatePicker datePicker = new DatePicker();

        Label customerIdentifierLabel = new Label("If you have an account with us, enter your customer ID. Otherwise, enter your name:");
        TextField customerIdentifier = new TextField();


        VBox select = new VBox();

        datePicker.setOnAction((e) -> {
            select.getChildren().clear();
            List<String> times = currentRestaurant.getAvailableStartTimes();
            times.forEach(t -> {
                Button time = new Button(t);
                select.getChildren().add(time);
                time.setOnAction(v -> {
                    dateTime = datePicker.getValue().atTime(LocalTime.parse(t));
                    select.getChildren().clear();

                });
            });
        });

        numOfPeople.setOnAction(e -> {
            try {
                Integer.parseInt(numOfPeople.getText());
                List<Table> availableTables = currentRestaurant.getAvailableTables(dateTime, Integer.parseInt(numOfPeople.getText()));
                availableTables.forEach(t -> {
                    Button table = new Button(t.toString());
                    select.getChildren().add(table);
                    table.setOnAction(v -> {
                        chosenTable = t;
                        select.getChildren().clear();
                    });
                });
            } catch (NumberFormatException err) {

            }
        });

        customerIdentifier.setOnAction(e -> {
            currentRestaurant.createReservation(chosenTable, Integer.parseInt(numOfPeople.getText()), Integer.parseInt(customerIdentifier.getText()), dateTime);
        });

        createReservation.getChildren().addAll(datePickerLabel, datePicker);
        bp.setRight(select);
        createReservation.getChildren().addAll(numOfPeopleLabel, numOfPeople);
        createReservation.getChildren().addAll(customerIdentifierLabel, customerIdentifier);


        bp.setCenter(createReservation);

        GridPane grid = new GridPane();

        RestaurantManagementSystem rms = new RestaurantManagementSystem();
        currentRestaurant = rms.getRestaurants().get(0);


//        rms.getRestaurants().forEach(r -> {
//            Button b = new Button(r.getName());
//            b.setOnAction(e -> {
//                setCurrentRestaurant(r);
//            });
//            grid.add(b, 0, 0);
//        });

        scene = new Scene(root, 1000, 300);
        stage.setTitle("Restaurant Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void setCurrentRestaurant(Restaurant r) {
        currentRestaurant = r;
        System.out.println("set current restaurant to " + r.getName());
    }

}