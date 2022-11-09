import java.util.ArrayList;


public class Waiter {

    private int tableID;
    private int customerID; /*dunno if this is needed */
    private int waiterID; /* dunno if this is really needed */
    private String status;
    private ArrayList<FoodItem> Order;

    public void newOrderFood(FoodItem food) {
        Order.add(food);
    }

    public void removeFood(FoodItem food) {
        Order.remove(food);
    }

    public void newOrder(int tableID, int customerID) {
        this.tableID = tableID;
        this.customerID = customerID;

    }

    public boolean setStatus(String status) {
        if (status == "served") {
            return true;
        }
    }

}
