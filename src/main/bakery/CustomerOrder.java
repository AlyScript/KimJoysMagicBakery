package bakery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a customer's order within a bakery system, detailing the items requested, their status, and other relevant attributes.
 * This class manages the lifecycle of an order from its creation to fulfillment, including potential garnishing and customer wait management.
 * It supports operations to check the availability of required ingredients, fulfill the order, and update its status accordingly.
 *
 * Usage involves creating an instance with specific ingredients and requirements, after which the order can be processed based on the available stock and customer needs.
 *
 * @author Adam Aly
 * @version 1.1
 * @since 2023
 * @see Ingredient
 * @see CustomerOrderStatus
 */
public class CustomerOrder implements java.io.Serializable {

    /**
     * Enumerates the possible statuses of a CustomerOrder within the bakery system.
     * Each status represents a different stage in the order processing lifecycle.
     *
     * - WAITING: The initial status of an order when it is first created.
     * - FULFILLED: Indicates that all required ingredients for the order have been successfully gathered.
     * - GARNISHED: Shows that the order has been not only fulfilled but also garnished as per the order requirements.
     * - IMPATIENT: Used to mark orders where the customer is waiting beyond a certain expected time.
     * - GIVEN_UP: Assigned to orders that are abandoned or cannot be completed as requested.
     */
    public enum CustomerOrderStatus {
        WAITING, FULFILLED, GARNISHED, IMPATIENT, GIVEN_UP
    }

    private CustomerOrderStatus status;
    private List<Ingredient> garnish = new ArrayList<>();
    private int level;
    private String name;
    private List<Ingredient> recipe = new ArrayList<>();
    private static final long serialVersionUID = 11085168;

    /**
     * Constructs a new CustomerOrder with specified details about the order.
     * Initializes the order with a name, recipe, garnish, and level, and sets the initial status to WAITING.
     *
     * @param name The name of the customer or the order identifier.
     * @param recipe A list of ingredients required to prepare the order.
     * @param garnish A list of ingredients used as garnish.
     * @param level The priority level of the order.
     */
    public CustomerOrder(String name, List<Ingredient> recipe, List<Ingredient> garnish, int level) {
        if(recipe == null || recipe.isEmpty())  {
            throw new WrongIngredientsException(name + " must have at least one ingredient in the recipe");
        }
        this.name = name;
        this.recipe = recipe;
        this.garnish = garnish;
        this.level = level;
        this.status = CustomerOrderStatus.WAITING;
    }
    
    /**
     * Determines if the order can be fulfilled based on the available ingredients.
     *
     * @param ingredients A list of available ingredients.
     * @return true if the order can be fulfilled, false otherwise.
     */
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

    /**
     * Determines if the order can be garnished based on the available ingredients.
     * @param ingredients A list of available ingredients.
     * @return true if the order can be garnished, false otherwise.
     */
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

    /**
     * Attempts to fulfill this order using the specified ingredients and optionally garnishes the order if required and possible.
     * Verifies the availability of required ingredients against the order's requirements before proceeding. If fulfillment is
     * achievable, the order status is updated to FULFILLED. If the garnish flag is true and conditions allow, it garnishes the order,
     * updating the status to GARNISHED.
     *
     * @param ingredients A list of {@link Ingredient} objects available to fulfill and optionally garnish the order. Must not be null.
     * @param garnish A boolean indicating whether the order should also be garnished after being fulfilled.
     * @return A list of {@link Ingredient} objects representing the ingredients used to fulfill and, if applicable, garnish the order.
     * @throws WrongIngredientsException if the order cannot be fulfilled with the provided ingredients.
     */
    public List<Ingredient> fulfill(List<Ingredient> ingredients, boolean garnish) throws WrongIngredientsException {
        if(!canFulfill(ingredients)) {
            throw new WrongIngredientsException("Cannot fulfill order");
        }
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

    /**
     * Returns the garnish ingredients associated with this order.
     *
     * @return A list of Ingredient objects used as garnish.
     */
    public List<Ingredient> getGarnish(){
        return garnish;
    }

    /**
     * Gets the garnish description in a formatted string.
     *
     * @return A string describing the garnish ingredients.
     */
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

    /**
     * Retrieves the level of urgency or priority of the order.
     *
     * @return An integer representing the level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the recipe ingredients of the order.
     *
     * @return A list of Ingredient objects used for the recipe.
     */
    public List<Ingredient> getRecipe() {
        return recipe;
    }

    /**
     * Gets the recipe description in a formatted string.
     *
     * @return A string describing the recipe ingredients.
     */
    public String getRecipeDescription() {
        String result = "";
        for(int i=0; i<recipe.size()-1; i++) {
            result += recipe.get(i).toString() + ", ";
        }
        result += recipe.get(recipe.size()-1).toString();
        return result;
    }

    /**
     * Returns a string representation of the customer order, typically the name of the customer or the order.
     *
     * @return A string representing the customer order.
     */
    public String toString() {
        return name;
    }

    /**
     * Retrieves the current status of the customer order.
     *
     * @return The current status of the order as an instance of CustomerOrderStatus.
     */
    public CustomerOrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the customer order.
     *
     * @param status The new status to set for this order.
     */
    public void setStatus(CustomerOrderStatus status) {
        this.status = status;
    }

    /**
     * Abandons the order, setting its status to GIVEN_UP.
     */
    public void abandon() {
        this.status = CustomerOrderStatus.GIVEN_UP;
    }
}
