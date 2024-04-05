package bakery;
import java.util.ArrayList;
import java.util.List;

public class Player implements java.io.Serializable{
    private List<Ingredient> hand;
    private String name;
    private static final long serialVersionUID = 11085168;

    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
    }

    public void addToHand(List<Ingredient> ingredients) {
        hand.addAll(ingredients);
    }

    public void addToHand(Ingredient ingredient) {
        hand.add(ingredient);
    }

    public boolean hasIngredient(Ingredient ingredient) {
        return hand.contains(ingredient);
    }

    public void removeFromHand(Ingredient ingredient) {
        hand.remove(ingredient);
    }

    public List<Ingredient> getHand() {
        return hand;
    }

    // return number of helpful ducks in hand
    public int helpfulDuckCount() {
        int count = 0;
        for(Ingredient ingredient : hand) {
            if(ingredient.toString().equalsIgnoreCase("HELPFUL_DUCK")) {
                count++;
            }
        }
        return count;
    }

    public void removeHelpfulDuckFromHand() {
        for(int i=0; i<hand.size(); i++) {
            if(hand.get(i).toString().equalsIgnoreCase("HELPFUL_DUCK")) {
                hand.remove(i);
                return;
            }
        }
    }

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
