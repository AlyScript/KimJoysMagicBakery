package util;
import bakery.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;

/**
 * Provides static utility methods for reading and interpreting files into usable objects within a bakery management system.
 * This class processes files containing data about ingredients, layers, and customer orders, transforming raw text into structured objects.
 * It is designed to facilitate the initial data setup for the application by loading pre-defined configurations.
 *
 * Note: This class is not intended to be instantiated as it serves purely functional static methods.
 *
 * @author Adam Aly
 * @version 1.0
 * @since 1.0
 */
public class CardUtils {

    private CardUtils() {
        
    }
    
    /**
     * Reads a file containing customer orders and returns a list of CustomerOrder objects.
     * Each line in the file represents a customer order, and should not start with "LEVEL" to be processed.
     *
     * @param path the file path where the customer orders are stored
     * @param layers a collection of layers that might be referenced by the customer orders
     * @return a list of CustomerOrder objects parsed from the file
     * @throws FileNotFoundException if the specified file could not be found
     */
    public static List<CustomerOrder> readCustomerFile(String path, Collection<Layer> layers) throws FileNotFoundException {
        ArrayList<CustomerOrder> result = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while((line = reader.readLine()) != null) {
                if(!line.startsWith("LEVEL")) {
                    result.add(stringToCustomerOrder(line, layers));
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new FileNotFoundException();
        }
        return result;
    }

    /**
     * Reads a file containing ingredients and returns a list of Ingredient objects.
     * Each line in the file represents an ingredient, and should not start with "NAME".
     *
     * @param path the file path of the ingredient data file
     * @return a list of Ingredient objects parsed from the file
     * @throws FileNotFoundException if the specified file could not be found
     */
    public static List<Ingredient> readIngredientFile(String path) throws FileNotFoundException {
        ArrayList<Ingredient> result = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while((line = reader.readLine()) != null) {
                if(!line.startsWith("NAME")) {
                    for (Ingredient ingredient : stringToIngredients(line)) {
                        result.add(ingredient);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new FileNotFoundException();
        }
        return result;
    }

    /**
     * Reads a file containing layers and returns a list of Layer objects.
     * Each line in the file represents a layer, and should follow a specific format to be parsed correctly.
     *
     * @param path the file path of the layer data file
     * @return a list of Layer objects parsed from the file
     * @throws FileNotFoundException if the specified file could not be found
     */
    public static List<Layer> readLayerFile (String path) throws FileNotFoundException {
        ArrayList<Layer> result = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            while((line = reader.readLine()) != null) {
                if(!line.startsWith("NAME")) {
                    for (Layer layer : stringToLayers(line)) {
                        result.add(layer);
                    }
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new FileNotFoundException();
        }
        return result;
    }

    // Helper method added to help with the stringToCustomerOrder
    // Make hashmap so we can get layers by name
    private static HashMap<String, Layer> buildLayerMap(ArrayList<Layer> layers) {
        HashMap<String, Layer> layerMap = new HashMap<>(); 
        for(int i=0; i<layers.size(); i++) {
            layerMap.put(layers.get(i).toString(), layers.get(i));
        }
        return layerMap;
    }
 
    private static CustomerOrder stringToCustomerOrder(String str, Collection<Layer> layers) {
        HashMap<String, Layer> layerMap = buildLayerMap((ArrayList<Layer>) layers);
        String[] fileString = str.split(",");
        int level = Integer.parseInt(fileString[0].strip());
        String name = fileString[1].strip();
        String[] recipeStringArr = fileString[2].split(";");
        String[] garnishStringArr;
        if (fileString.length > 3) {
            garnishStringArr = fileString[3].split(";");
        } else {
            garnishStringArr = new String[0]; // empty array
        }
        ArrayList<Ingredient> recipe = new ArrayList<>();
        ArrayList<Ingredient> garnish = new ArrayList<>();
        for(String ingredient : recipeStringArr) {
            ingredient = ingredient.strip();
            if(layerMap.containsKey(ingredient)) {
                recipe.add(layerMap.get(ingredient));
            } else {
                recipe.add(new Ingredient(ingredient));
            }
        }
        for(String ingredient : garnishStringArr) {
            ingredient = ingredient.strip();
            if(layerMap.containsKey(ingredient)) {
                garnish.add(layerMap.get(ingredient));
            } else {
                garnish.add(new Ingredient(ingredient));
            }
        }
        return new CustomerOrder(name, recipe, garnish, level);
    }
    
    private static List<Ingredient> stringToIngredients(String str) {
        ArrayList<Ingredient> result = new ArrayList<>();
        String[] nameCount = str.split(",");
        String ingredientName = nameCount[0];
        int count = Integer.parseInt(nameCount[1].strip());
        for(int i = 0; i < count; i++) {
            result.add(new Ingredient(ingredientName));
        }
        return result;
    }

    private static List<Layer> stringToLayers(String str) {
        ArrayList<Layer> result = new ArrayList<>();
        ArrayList<Ingredient> recipe = new ArrayList<>();
        String[] nameRecipe = str.split(",");
        String layerName = nameRecipe[0];
        String[] recipeList = nameRecipe[1].split(";");
        for (String string : recipeList) {
            recipe.add(new Ingredient(string.strip()));
        }
        int count = 4;
        for(int i = 0; i < count; i++) {
            result.add(new Layer(layerName, recipe));
        }
        return result;
    }
    
}
