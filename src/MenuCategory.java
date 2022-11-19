import java.util.ArrayList;

public class MenuCategory {

    private String name;
    private ArrayList<FoodItem> food = new ArrayList<>();

    public MenuCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addFoodItem(FoodItem f) {
        food.add(f);
    }

    public ArrayList<FoodItem> getFood() {
        return food;
    }

    @Override
    public String toString() {
        return "MenuCategory{" +
                "food=" + food +
                '}';
    }
}
