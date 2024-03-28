package bakery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import util.ConsoleUtils;

public class MagicBakery {
    private Collection<Layer> layers;
    private Collection<Player> players;
    private Collection <Ingredient> pantry;
    private Collection<Ingredient> pantryDeck;
    private Collection<Ingredient> pantryDiscard;
    private Random random;

    private int currentPlayerIndex;
    private int actionsUsed = 0;

    public enum ActionType {
        DRAW_INGREDIENT, PASS_INGREDIENT, BAKE_LAYER, FULFIL_ORDER, REFRESH_PANTRY
    }

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) {
        random = new Random(seed);
        players = new LinkedList<Player>();
        currentPlayerIndex = 0;
    }

    private Ingredient drawFromPantryDeck() {
        if (pantryDeck.isEmpty()) {
            return null;
        }
        Ingredient ingredient = pantryDeck.iterator().next();
        pantryDeck.remove(ingredient);
        return ingredient;
    }

    public void drawFromPantry(String ingredientName) {
        boolean found = false;
        for(Ingredient ingredient : pantry) {
            if(ingredient.toString().equalsIgnoreCase(ingredientName)) {
                getCurrentPlayer().addToHand(ingredient);
                pantry.remove(ingredient);
                found = true;
                break;
            }
        }
        if(!found) {
            System.out.println("Ingredient not found in pantry.");
        }
        actionsUsed++;
    }

    public void drawFromPantry(Ingredient ingredient) {
        if(pantry.contains(ingredient)) {
            getCurrentPlayer().addToHand(ingredient);
            pantry.remove(ingredient);
        } else {
            System.out.println("Ingredient not found in pantry.");
        }
        actionsUsed++;
    }

    public boolean endTurn() {
        if (getActionsRemaining() > 0) {
            System.out.println("You still have actions remaining.");
            return false;
        }
    
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    
        if (currentPlayerIndex == 0) {
            System.out.println("New round");
        }
    
        return true;
    }

    public int getActionsPermitted() {
        if(players.size() >= 4) {
            return 2;
        }
        return 3;
    }

    public int getActionsRemaining() {
        return getActionsPermitted() - actionsUsed;
    }

    public LinkedList<Player> getPlayers() {
        return new LinkedList<>(this.players);
    }

    public Player getCurrentPlayer() {
        return getPlayers().get(currentPlayerIndex);
    }

    public void passIngredient(Ingredient ingredient, Player recipient) {
        if (getActionsRemaining() > 0) {
            getCurrentPlayer().removeFromHand(ingredient);
            recipient.addToHand(ingredient);
            actionsUsed++;
        } else {
            System.out.println("No actions remaining.");
        }
    }

    public void refreshPantry() {
        pantryDeck.addAll(pantryDiscard);
        pantryDiscard.clear();
        Collections.shuffle((List) pantryDeck, random);
        for(int i=0; i<pantryDeck.size(); i++) {
            pantry.add(drawFromPantryDeck());
        }
        actionsUsed++;
    }

    public void startGame(List<String> playerNames, String customerDeckFile) {
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        Collections.shuffle((List) pantryDeck, random);
        for(int i=0; i<pantryDeck.size(); i++) {
            pantry.add(drawFromPantryDeck());
        }
        for(Player player : players) {
            for(int i=0; i<3; i++) {
                player.addToHand(drawFromPantryDeck());
            }
        }
    }

}
