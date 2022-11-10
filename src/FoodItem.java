public class FoodItem {
    public double price;
    public String name;

    public String ToString() {
        return String.format("%s30 %f", name, price);
    }
}
