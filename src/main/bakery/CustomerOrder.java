package bakery;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrder implements java.io.Serializable {

    public enum CustomerOrderStatus {
        WAITING, FULFILLED, GARNISHED, IMPATIENT, GIVEN_UP
    }

    private CustomerOrderStatus status;
    private List<Ingredient> garnish = new ArrayList<>();
    private int level;
    private String name;
    private List<Ingredient> recipe = new ArrayList<>();
    private static final long serialVersionUID = 11085168;

    public CustomerOrder(String name, List<Ingredient> recipe, List<Ingredient> garnish, int level) {
        this.name = name;
        this.recipe = recipe;
        this.garnish = garnish;
        this.level = level;
        this.status = CustomerOrderStatus.WAITING;
    }
    
    public boolean canFulfill(List<Ingredient> ingredients) {
        return ingredients.containsAll(recipe);
    }

    public boolean canGarnish(List<Ingredient> ingredients) {
        return ingredients.containsAll(garnish);
    }

    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish) {
        List<Ingredient> usedRecipeIngredients = new ArrayList<>();
        List<Ingredient> usedGarnishIngredients = new ArrayList<>();
        int helpfulDuckCount = 0;
        for(Ingredient ingredient : ingredients) {
            if(ingredient.toString().equalsIgnoreCase("HELPFUL_DUCK")) {
                helpfulDuckCount++;
            }
        }

        for(Ingredient ingredient : recipe) {
            if(ingredients.contains(ingredient)) {
                usedRecipeIngredients.add(ingredient);
                recipe.remove(ingredient);
            } else if(helpfulDuckCount > 0) {
                usedRecipeIngredients.add(Ingredient.HELPFUL_DUCK);
                helpfulDuckCount--;
                recipe.remove(Ingredient.HELPFUL_DUCK);
            }
        }
        if(usedRecipeIngredients.size() == recipe.size()) {
            status = CustomerOrderStatus.FULFILLED;
        }

        if(garnish && usedRecipeIngredients.size() == recipe.size()) {
            for(Ingredient ingredient : this.garnish) {
                if(ingredients.contains(ingredient)) {
                    usedGarnishIngredients.add(ingredient);
                    this.garnish.remove(ingredient);
                } else if(helpfulDuckCount > 0) {
                    usedGarnishIngredients.add(Ingredient.HELPFUL_DUCK);
                    helpfulDuckCount--;
                    this.garnish.remove(Ingredient.HELPFUL_DUCK);
                }
            }
            if(usedGarnishIngredients.size() == this.garnish.size()) {
                status = CustomerOrderStatus.GARNISHED;
            }
            usedRecipeIngredients.addAll(usedGarnishIngredients);
        }

        return (List) usedRecipeIngredients;
    }

    public List<Ingredient> getGarnish(){
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

    public List<Ingredient> getRecipe() {
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
        if(garnish.size() > 0) {
            return String.format("Name: %s, Recipe: %s, Garnish: %s, Level: %d", name, recipe, garnish, level);
        }
        return String.format("Name: %s, Recipe: %s, Level: %d", name, recipe, level);
    }

    public CustomerOrderStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerOrderStatus status) {
        this.status = status;
    }

    public void abandon() {
        this.status = CustomerOrderStatus.GIVEN_UP;
    }
}
