import java.util.ArrayList;

public class Menu {

    ArrayList<MenuCategory> menu = new ArrayList<>();

    public Menu() {

    }


//    public Menu(TreeMap<String, MenuCategory> menu) {
//        this.menu = menu;
//    }

    public ArrayList<MenuCategory> getCategories() {
        return menu;
    }

}