package bakery;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a layer in a bakery product, extending the functionality of an Ingredient. Each layer is characterized by
 * a unique name and a specific recipe, which comprises a list of ingredients necessary to prepare the layer. This class
 * is crucial for modeling the complexity of bakery items that consist of multiple layers, each with distinct ingredients
 * and preparation requirements.
 * 
 * In addition to basic ingredient properties, the Layer class provides mechanisms to verify if a given set of ingredients
 * is sufficient to prepare the layer, accounting for the possibility of substitutions by a special 'helpful duck' ingredient.
 * This functionality supports the dynamic preparation environment of a bakery, where ingredient availability may vary.
 * 
 * The class is designed to be used in conjunction with other classes that manage bakery operations, such as inventory management,
 * order processing, and recipe formulation. It ensures that each layer can be independently verified and processed, enhancing
 * the modularity and scalability of the system.
 * 
 * Usage of this class within the system typically involves creating instances for each layer of a product as defined by a recipe
 * and then using these instances to check readiness for production based on current ingredient stocks.
 * 
 * @author Adam Aly
 * @version 1.2
 * @since 2023-04-01
 */
public class Layer extends Ingredient{
    private List<Ingredient> recipe;
    private static final long serialVersionUID = 11085168;
     
    /**
     * Constructs a new Layer with the specified name and recipe. It initializes the Layer with a list of ingredients
     * required to create it. If the recipe is null or empty, a WrongIngredientsException is thrown.
     *
     * @param name The name of the layer, which is also used as the name of the base ingredient.
     * @param recipe A list of ingredients required to bake the layer. Must not be null or empty.
     * @throws WrongIngredientsException if the recipe is null or empty, indicating invalid or insufficient specifications for the layer.
     */
    public Layer(String name, List<Ingredient> recipe) throws WrongIngredientsException {
        super(name);    
        if(recipe == null) {
            throw new WrongIngredientsException("Recipe cannot be null");
        }
        this.recipe = recipe;
        if(recipe.isEmpty()) {
            throw new WrongIngredientsException("Recipe cannot be empty");
        }
    }

    /**
     * Checks if the layer can be baked with the given set of ingredients. This method verifies whether all ingredients
     * required for the layer's recipe are present in the provided list of ingredients. A special 'helpful duck' ingredient
     * can substitute for any missing ingredient.
     *
     * @param ingredients A list of ingredients available to attempt baking the layer.
     * @return true if all required ingredients are present or can be substituted by the 'helpful duck'; false otherwise.
     */
    public boolean canBake(List<Ingredient> ingredients) {
        List<Ingredient> ingredientsCopy = new ArrayList<>(ingredients);
        for (Ingredient ingredient : recipe) {
            if (!ingredientsCopy.remove(ingredient) && !ingredientsCopy.remove(Ingredient.HELPFUL_DUCK)) {
                return false;
            }
        }
        return true;
    }
    
    /** Getter for recipe
    *   @param none
    *   @return Returns a list of ingredients in recipe
    */
    public List<Ingredient> getRecipe() {
        return recipe;
    }

    /** Getter for recipe description
    *   @param none
    *   @return Returns a comma separated list (String) of ingredients in recipe
    */
    public String getRecipeDescription() {
        String result = "";
        for(int i=0; i<recipe.size()-1; i++) {
            result += recipe.get(i).toString() + ", ";
        }
        result += recipe.get(recipe.size()-1).toString();
        return result;
    }

    /** Generate a hash code for the layer
     * @param none
     * @return Returns a hash code for the layer
     */
    public int hashCode() {
        recipe.sort(null);
        return recipe.hashCode();
    }

}
