package bakery;

import java.util.ArrayList;
import java.util.List;


public class Layer extends Ingredient{
    private List<Ingredient> recipe;
    private static final long serialVersionUID = 11085168;
     
    /** Constructor for Layer class
    *   @param name Name of the layer
    *   @param recipe List of ingredients in the recipe
    *   @return Layer object
    */
    public Layer(String name, List<Ingredient> recipe) {
        super(name);    
        this.recipe = recipe;
    }

     /** Check if the player has all the ingredients to bake the layer
     * @param ingredients
     * @return true if the player has all the ingredients to bake the layer
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
