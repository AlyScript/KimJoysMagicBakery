package bakery;

import java.util.ArrayList;

public class CustomerOrder {
    private ArrayList<Ingredient> garnish = new ArrayList<>();
    private int level;
    private String name;
    private ArrayList<Ingredient> recipe = new ArrayList<>();

    public CustomerOrder(String name, ArrayList<Ingredient> recipe, ArrayList<Ingredient> garnish, int level) {
        this.name = name;
        this.recipe = recipe;
        this.garnish = garnish;
        this.level = level;
    }

    public ArrayList<Ingredient> getGarnish(){
        return garnish;
    }

    public String getGarnishDescription() {
        String result = "";
        for(int i=0; i<recipe.size()-1; i++) {
            result += recipe.get(i).toString() + ", ";
        }
        result += recipe.get(recipe.size()-1).toString();
        return result;
    }

    public int getLevel() {
        return level;
    }

    public ArrayList<Ingredient> getRecipe() {
        return recipe;
    }

    public String getRecipeDescription() {
        String result = "";
        for(int i=0; i<recipe.size()-1; i++) {
            result += recipe.get(i).toString() + ", ";
        }
        result += recipe.get(recipe.size()-1).toString();
        return result;
    }

    public String toString() {
        return String.format("%s, %s, %s, %d", name, recipe, garnish, level);
    }
}
