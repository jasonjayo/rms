import java.util.ArrayList;

public class Order {
    private int tableID;
    private ArrayList<FoodItem> newOrder;
    private double billTotal;
    private double tip;
    private String orderStatus;

    /**
     * Constructs Order Object
     */
    public Order(int tableID) {
        this.tableID = tableID;
        newOrder = new ArrayList<FoodItem>();

    }

    public void addFood(FoodItem food) {
        newOrder.add(food);

    }

    public void removeFood(FoodItem food) {
        newOrder.remove(food);
    }

    public void getBillTotal() {
        //getting total amount to pay from all food items in Order
    }

    public void setTip() {
        // set tip to pay which will be added to bill when printed
    }

    public String toString() { //printing the bill method?
        //print Bill total and tip as amount due to pay
        return null;
    }

    public void setOrderStatus() {
        /*
        maybe the orderStatus should use an array of strings of statuses,
         which I then change by incrementing i, after a wee timer of like
         30 seconds 0.0
         */
    }
//this is all I have for now plz dont judge >:(

}
