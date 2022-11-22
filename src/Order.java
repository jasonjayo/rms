import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Order {

    enum Status {
        TAKEN,
        IN_KITCHEN,
        READY,
        SERVED,
        PAID
    }

    private int tableID;

    private int orderID;
    private int restaurantID;
    private ArrayList<FoodItem> newOrder;

    String[] orderStatuses = {"Ordered", "In da kitchen", "On the way", "Served, enjoy!"};


    private double tip;
    private Status orderStatus;

//    private static final TAKEN;

    /**
     * Constructs Order Object
     */
    public Order(int tableID, int restaurantID) {
        this.tableID = tableID;
        this.restaurantID = restaurantID;

        newOrder = new ArrayList<FoodItem>();

    }

    public void addFood(FoodItem food) {
        newOrder.add(food);

    }

    public void removeFood(FoodItem food) {
        newOrder.remove(food);
    }

    public double getBillTotal() {
        double billTotal = 0;
        //getting total amount to pay from all food items in Order
        for (FoodItem food : newOrder) {
            billTotal += food.price;
        }
        billTotal += tip;
        return billTotal;
    }

    public void setTip(double aTip) {
        this.tip = aTip;
    }

    public double getTip() {
        return tip;
    }

    public String toString() { //printing the bill method?
        //print Bill total and tip as amount due to pay
        String nameAndPrice = "";
        for (FoodItem food : newOrder) {
            nameAndPrice += food + "\n";
        }
        String bill = String.format("%-20s %s%.2f\n", "Total bill is ", "EUR ", getBillTotal());
        String tip = String.format("%-20s %s%.2f\n", "Added tip is ", "EUR ", getTip());
        return nameAndPrice + "=".repeat(30) + "\n" + bill + tip;
//MIGHT NEED FIXING !!!!
    }

    public double payBillByCash(double amt) {
        if (amt < getBillTotal()) {
            return -1;
        }
        setOrderStatus(Status.PAID);
        return amt - getBillTotal();
    }

    public boolean payBillByCard(String cardNum, String expiry, String csv) {
        Pattern expiryPattern = Pattern.compile("[0-9]{2}/[0-9]{4}");
        if (cardNum.length() != 16) {
            // check card number
            return false;
        }
        if (csv.length() != 3) {
            // check CSV
            return false;
        }
        if (!expiryPattern.matcher(expiry).find()) {
            // check expiry
            return false;
        }
        setOrderStatus(Status.PAID);
        return true;
    }

    public void setOrderStatus(Status s) { // 0.ordered 1.in da kitchen 2. en route 3.served, enjoy!

        if (s == Status.TAKEN) {
            saveOrder();
        }

        this.orderStatus = s;

        /*
        maybe the orderStatus should use an array of strings of statuses,
         which I then change by incrementing i, after a wee timer of like
         30 seconds 0.0
         RESEARCH THIS! QUEUING !!!
         have an array of strings of differet statuses, and setOrderStatus should take in an integer from 1-4, which corresponds to the index of the statuses in the array.
         */
    }

    public Status getStatus() {
        return orderStatus;
    }

    public void saveOrder() {
        // format
        // total, datetime

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);


        String orderForCSV = String.format("%s,%s,%s\n", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), restaurantID, getBillTotal());

        try {
            Files.write(Paths.get("orders.csv"), orderForCSV.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    //  public void noOfPossibleStatuses() {
//    return statuses.length;
    //}


}
