package bakery;

public class Ingredient {
    private String name;
    public static Ingredient HELPFUL_DUCK;

    public Ingredient() {
        
    }
    
    public Ingredient (String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
