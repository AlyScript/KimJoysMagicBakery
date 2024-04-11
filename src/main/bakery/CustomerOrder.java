package bakery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
        Map<Ingredient, Integer> ingredientCountMap = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            ingredientCountMap.put(ingredient, ingredientCountMap.getOrDefault(ingredient, 0) + 1);
        }
    
        for (Ingredient recipeIngredient : recipe) {
            if (!ingredientCountMap.containsKey(recipeIngredient) || ingredientCountMap.get(recipeIngredient) == 0) {
                if (recipeIngredient instanceof Layer || !ingredientCountMap.containsKey(Ingredient.HELPFUL_DUCK) || ingredientCountMap.get(Ingredient.HELPFUL_DUCK) == 0) {
                    return false;
                } else {
                    ingredientCountMap.put(Ingredient.HELPFUL_DUCK, ingredientCountMap.get(Ingredient.HELPFUL_DUCK) - 1);
                }
            } else {
                int count = ingredientCountMap.get(recipeIngredient) - 1;
                if (count == 0) {
                    ingredientCountMap.remove(recipeIngredient);
                } else {
                    ingredientCountMap.put(recipeIngredient, count);
                }
            }
        }
    
        return true;
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
        Map<Ingredient, Integer> ingredientCountMap = new HashMap<>();
        for (Ingredient ingredient : ingredients) {
            ingredientCountMap.put(ingredient, ingredientCountMap.getOrDefault(ingredient, 0) + 1);
        }
    
        for (Ingredient garnishIngredient : garnish) {
            if (!ingredientCountMap.containsKey(garnishIngredient) || ingredientCountMap.get(garnishIngredient) == 0) {
                if (garnishIngredient instanceof Layer) {
                    return false;
                } else if (!ingredientCountMap.containsKey(Ingredient.HELPFUL_DUCK) || ingredientCountMap.get(Ingredient.HELPFUL_DUCK) == 0) {
                    return false;
                } else {
                    ingredientCountMap.put(Ingredient.HELPFUL_DUCK, ingredientCountMap.get(Ingredient.HELPFUL_DUCK) - 1);
                }
            } else {
                int count = ingredientCountMap.get(garnishIngredient) - 1;
                if (count == 0) {
                    ingredientCountMap.remove(garnishIngredient);
                } else {
                    ingredientCountMap.put(garnishIngredient, count);
                }
            }
        }
    
        return true;
    }

    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish) {
        List<Ingredient> availableIngredients = new ArrayList<>(ingredients);
        int helpfulDuckCount = Collections.frequency(availableIngredients, Ingredient.HELPFUL_DUCK);
        List<Ingredient> usedIngredients = new ArrayList<>();

        for(Ingredient ingredient : this.recipe) {
            if(availableIngredients.contains(ingredient)) {
                availableIngredients.remove(ingredient);
                usedIngredients.add(ingredient);
            } else if(helpfulDuckCount > 0) {
                helpfulDuckCount--;
                availableIngredients.remove(Ingredient.HELPFUL_DUCK);
                usedIngredients.add(Ingredient.HELPFUL_DUCK);
            }
        }

        setStatus(CustomerOrderStatus.FULFILLED);

        if(garnish && canGarnish(availableIngredients) && this.garnish.size() > 0){
            for(Ingredient ingredient : this.garnish) {
                if(availableIngredients.contains(ingredient)) {
                    availableIngredients.remove(ingredient);
                    usedIngredients.add(ingredient);
                } else if(helpfulDuckCount > 0) {
                    helpfulDuckCount--;
                    availableIngredients.remove(Ingredient.HELPFUL_DUCK);
                    usedIngredients.add(Ingredient.HELPFUL_DUCK);
                }
            }
            setStatus(CustomerOrderStatus.GARNISHED);
        }

        return usedIngredients;
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
