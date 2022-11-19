public class FoodItem {

    public String name;
    public double price;


    public FoodItem(String name, double price) {
        this.price = price;
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%-20s EUR %.2f", name, price);
    }
}
