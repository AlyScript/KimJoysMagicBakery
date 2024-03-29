package bakery;

public class Ingredient {
    private String name;
    public static final Ingredient HELPFUL_DUCK = new Ingredient();

    public Ingredient() {
        
    }
    
    public Ingredient (String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
