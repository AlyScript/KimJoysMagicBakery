package bakery;

import java.util.ArrayList;
import java.util.List;

public class Layer extends Ingredient{
    private List<Ingredient> recipe;
    private static final long serialVersionUID = 11085168;
     
    public Layer(String name, List<Ingredient> recipe) {
        super(name);    
        this.recipe = recipe;
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

}
