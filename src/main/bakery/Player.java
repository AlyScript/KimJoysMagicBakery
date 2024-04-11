package bakery;
import java.util.ArrayList;
import java.util.List;

public class Player implements java.io.Serializable{
    private List<Ingredient> hand;
    private String name;
    private static final long serialVersionUID = 11085168;

    /** Constructor for Player class
     *   @param name Name of the player
     *   @return Player object
     */
    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
    }

    /** add a number of ingredients to the player's hand
     *   @param ingredients
     *   @return none
     */
    public void addToHand(List<Ingredient> ingredients) {
        hand.addAll(ingredients);
    }

    /** Add an ingredient to the player's hand
     *   @param ingredient Ingredient to be added to the player's hand
     *   @return void
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

    /** Remove an ingredient from the player's hand
     * 
     *   @param Ingredient to be removed from the player's hand
     */
    public void removeFromHand(Ingredient ingredient) {
        hand.remove(ingredient);
    }

    /** Getter for name
     *   @param none
     *   @return Returns the name of the player
     */
    public List<Ingredient> getHand() {
        return hand;
    }

    /** Count the number of helpful ducks in the player's hand (NOT IN UML/SPEC)
     *   @param none
     *   @return Returns the number of helpful ducks in the player's hand
     */
    public int helpfulDuckCount() {
        int count = 0;
        for(Ingredient ingredient : hand) {
            if(ingredient.toString().equalsIgnoreCase("HELPFUL_DUCK")) {
                count++;
            }
        }
        return count;
    }

    /** Remove a helpful duck from the player's hand (NOT IN UML/SPEC)
     *   @param none
     *   @return none
     */
    public void removeHelpfulDuckFromHand() {
        for(int i=0; i<hand.size(); i++) {
            if(hand.get(i).toString().equalsIgnoreCase("HELPFUL_DUCK")) {
                hand.remove(i);
                return;
            }
        }
    }

    /** Return player's hand as a string
     *   @param none
     *   @return Players hand as comma separated string
     */
    public String getHandStr() {
        String result = "";
        for(int i=0; i<hand.size()-1; i++) {
            result += hand.get(i).toString() + ", ";
        }
        result += hand.get(hand.size()-1).toString();
        return result;
    }

    public String toString() {
        return name;
    }
}
