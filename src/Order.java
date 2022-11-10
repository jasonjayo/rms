import java.util.ArrayList;

public class Order {
    private int tableID;
    private ArrayList<FoodItem> newOrder;
    private double billTotal;

    String[] orderStatuses = {"Ordered", "In da kitchen", "On the way", "Served, enjoy!"};


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

    public double getBillTotal() {
        //getting total amount to pay from all food items in Order
        for (FoodItem food : newOrder) {
            billTotal += food.price;
        }
        return billTotal;
    }

    public void setTip(int aTip) {
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
        String bill = String.format("%30s%2f\n", "Total Bill is : ", getBillTotal());
        String tip = String.format("%30s%2f\n", "Added Tip is : ", getTip());
        return nameAndPrice + bill + tip;
//MIGHT NEED FIXING !!!!
    }

    public void setOrderStatus(int indexOfStatus) { // 0.ordered 1.in da kitchen 2. en route 3.served, enjoy!


        /*
        maybe the orderStatus should use an array of strings of statuses,
         which I then change by incrementing i, after a wee timer of like
         30 seconds 0.0
         RESEARCH THIS! QUEUING !!!
         have an array of strings of differet statuses, and setOrderStatus should take in an integer from 1-4, which corresponds to the index of the statuses in the array.
         */
    }

  //  public void noOfPossibleStatuses() {
//    return statuses.length;
    }


}
