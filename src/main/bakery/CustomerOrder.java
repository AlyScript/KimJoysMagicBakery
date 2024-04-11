package bakery;

import java.util.ArrayList;
import java.util.Collections;
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
        int helpfulDuckCount = Collections.frequency(ingredients, Ingredient.HELPFUL_DUCK);
        int ingredientCount = 0;
        if(ingredients.containsAll(recipe)) {
            return true;
        } else {
            for(Ingredient recipeIngredient : recipe) {
                if(ingredients.contains(recipeIngredient)) {
                    ingredientCount++;
                }
            }
        }
        if(ingredientCount + helpfulDuckCount == recipe.size()) {
            return true;
        }
        return false;
    }

    // get the ingredients used to fulfill the recipe portion of the order
    // helper method for canGarnish
    public List<Ingredient> simulateFulfill(List<Ingredient> ingredients) {
        List<Ingredient> ingredientsCopy = new ArrayList<>(ingredients);
        int helpfulDuckCount = Collections.frequency(ingredientsCopy, Ingredient.HELPFUL_DUCK);
    
        for(Ingredient ingredient : this.recipe) {
            if(ingredientsCopy.contains(ingredient)) {
                ingredientsCopy.remove(ingredient);
            } else if(helpfulDuckCount > 0) {
                helpfulDuckCount--;
                ingredientsCopy.remove(Ingredient.HELPFUL_DUCK);
            }
        }
    
        return ingredientsCopy;
    }

    public boolean canGarnish(List<Ingredient> ingredients) {
        List<Ingredient> remainingIngredients = simulateFulfill(ingredients);
        List<Ingredient> usedGarnishIngredients = new ArrayList<>();
        int helpfulDuckCount = Collections.frequency(remainingIngredients, Ingredient.HELPFUL_DUCK);
    
        for(Ingredient ingredient : this.garnish) {
            if(remainingIngredients.contains(ingredient)) {
                usedGarnishIngredients.add(ingredient);
                remainingIngredients.remove(ingredient);
            } else if(helpfulDuckCount > 0) {
                usedGarnishIngredients.add(Ingredient.HELPFUL_DUCK);
                helpfulDuckCount--;
                remainingIngredients.remove(Ingredient.HELPFUL_DUCK);
            }
        }
    
        return usedGarnishIngredients.size() == this.garnish.size();
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

        for(int i=0; i<recipe.size(); i++){
            Ingredient ingredient = recipe.get(i);
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
            for(int i=0; i<this.garnish.size(); i++) {
                Ingredient ingredient = this.garnish.get(i);
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
        if(garnish.size() > 1) {
            for(int i=0; i<garnish.size()-1; i++) {
                result += garnish.get(i).toString() + ", ";
            }
        }
        if(garnish.size() > 0) {
            result += garnish.get(garnish.size()-1).toString();
        }
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
        return name;
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
