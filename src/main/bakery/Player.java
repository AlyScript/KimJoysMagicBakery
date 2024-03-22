package bakery;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Ingredient> hand;
    private String name;

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
