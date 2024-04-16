package bakery;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a player in a bakery-themed game, managing a collection of ingredients known as the player's hand.
 * This class is central to the gameplay, as it handles the storage, retrieval, and management of ingredients that players collect,
 * use, or exchange throughout the game. Each player is identified by a unique name, and their capabilities in the game are often
 * determined by the ingredients they possess.
 *
 * The Player class provides methods to add and remove ingredients, check for the presence of specific ingredients, and
 * get a string representation of the ingredients in hand. This functionality is crucial for implementing game rules related
 * to recipe completion and resource management.
 *
 * @author Adam Aly
 * @version 1.2
 * @since 2023-04-01
 * @see Ingredient
 */
public class Player implements java.io.Serializable{
    private List<Ingredient> hand;
    private String name;
    private static final long serialVersionUID = 11085168;

    /**
     * Constructs a new Player with the given name. The player's hand is initialized as an empty list of ingredients.
     * This setup is crucial for starting the game with a clean slate for each player.
     *
     * @param name the unique identifier for the player, used throughout the game to track player actions and status.
     */
    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
    }

    /**
     * Adds multiple ingredients to the player's hand. This method is typically used when the player acquires new ingredients.
     *
     * @param ingredients the list of ingredients to be added to the player's hand.
     */
    public void addToHand(List<Ingredient> ingredients) {
        hand.addAll(ingredients);
    }

    /**
     * Adds a single ingredient to the player's hand. This method is used when the player acquires one new ingredient.
     *
     * @param ingredient the ingredient to be added to the player's hand.
     */
    public void addToHand(Ingredient ingredient) {
        hand.add(ingredient);
    }

    /** Check if the player has a specific ingredient in their hand
     *   @param ingredient Ingredient to be checked
     *   @return true if the player has the ingredient in their hand
     */
    public boolean hasIngredient(Ingredient ingredient) {
        return hand.contains(ingredient);
    }

    /**
     * Removes a specified ingredient from the player's hand. This method is used when an ingredient is used up
     * or needs to be discarded from the player's inventory. If the specified ingredient is not found in the player's hand,
     * a WrongIngredientsException is thrown, indicating the absence of the ingredient.
     *
     * @param ingredient the ingredient to be removed from the player's hand. It must not be null.
     * @throws WrongIngredientsException if the ingredient is not present in the player's hand, with a detailed message
     *         stating the missing ingredient and the player's name.
     */
    public void removeFromHand(Ingredient ingredient) throws WrongIngredientsException {
        if(!hand.remove(ingredient)) {
            throw new WrongIngredientsException(name + " does not have " + ingredient + " in their hand");
        }
    }

    /** Getter for name
     *   @param none
     *   @return Returns the name of the player
     */
    public List<Ingredient> getHand() {
        hand.sort(Comparator.comparing(Ingredient::toString, String.CASE_INSENSITIVE_ORDER));
        return hand;
    }

    /** Count the number of helpful ducks in the player's hand (NOT IN UML/SPEC)
     *   @param none
     *   @return Returns the number of helpful ducks in the player's hand
     */
    public int helpfulDuckCount() {
        int count = 0;
        for(Ingredient ingredient : hand) {
            if(ingredient.equals(Ingredient.HELPFUL_DUCK)) {
                count++;
            }
        }
        return count;
    }

    /** Remove a helpful duck from the player's hand (NOT IN UML/SPEC)
     *   @param none
     *   @return none
     */
    public Ingredient removeHelpfulDuckFromHand() {
        return hand.remove(hand.indexOf(Ingredient.HELPFUL_DUCK));
    }

    /** Return player's hand as a string
     *   @param none
     *   @return Players hand as comma separated string
     */
    public String getHandStr() {
        if (hand.isEmpty()) {
            return "";
        }
    
        // Create a copy of the hand list and sort it
        List<Ingredient> sortedHand = getHand();
        //sortedHand.sort(Comparator.comparing(Ingredient::toString));
    
        StringBuilder result = new StringBuilder();
        String lastIngredient = sortedHand.get(0).toString();
        int count = 0;
    
        for (Ingredient ingredient : sortedHand) {
            String currentIngredient = ingredient.toString();
            if (!currentIngredient.equals(lastIngredient)) {
                result.append(capitaliseFirstLetter(lastIngredient));
                if (count > 1) {
                    result.append(" (x").append(count).append(")");
                }
                result.append(", ");
                lastIngredient = currentIngredient;
                count = 0;
            }
            count++;
        }
    
        result.append(capitaliseFirstLetter(lastIngredient));
        if (count > 1) {
            result.append(" (x").append(count).append(")");
        }
    
        return result.toString();
    }

    /**
     * Returns a string representation of the player, primarily the player's name.
     * This method is commonly used to display the player's identity in user interfaces or logs.
     *
     * @return The name of the player as a string.
     */
    public String toString() {
        return name;
    }

    String capitaliseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
