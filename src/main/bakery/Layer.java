package bakery;

import java.util.ArrayList;
import java.util.List;

public class Layer extends Ingredient{
    private ArrayList<Ingredient> recipe;
     
    public Layer(String name, ArrayList<Ingredient> recipe) {
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
