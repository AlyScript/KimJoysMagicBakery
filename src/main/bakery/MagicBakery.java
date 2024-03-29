package bakery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import util.CardUtils;

public class MagicBakery {
    private Customers customers;
    private Collection<Layer> layers;
    private Collection<Player> players;
    private Collection <Ingredient> pantry;
    private Collection<Ingredient> pantryDeck;
    private Collection<Ingredient> pantryDiscard;
    private Random random;
    private static final long serialVersionUID = 11085168;

    private int currentPlayerIndex;
    private int actionsUsed;

    public enum ActionType {
        DRAW_INGREDIENT, PASS_INGREDIENT, BAKE_LAYER, FULFIL_ORDER, REFRESH_PANTRY
    }

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) {
        layers = CardUtils.readLayerFile(layerDeckFile);
        players = new LinkedList<Player>();
        pantry = CardUtils.readIngredientFile(ingredientDeckFile);
        pantryDeck = new Stack<>();
        pantryDiscard = new Stack<>();
        random = new Random(seed);

        currentPlayerIndex = 0;
        actionsUsed = 0;
    }

    public void bakeLayer(Layer layer) {
        if(getBakeableLayers().contains(layer)) {
            Player currentPlayer = getCurrentPlayer();
            List<Ingredient> recipe = new ArrayList<>(layer.getRecipe());
    
            for (Ingredient ingredient : recipe) {
                if (currentPlayer.getHand().contains(ingredient)) {
                    currentPlayer.removeFromHand(ingredient);
                    pantryDiscard.add(ingredient);
                } else if (currentPlayer.helpfulDuckCount() >= 1 && !(ingredient instanceof Layer)) {
                    currentPlayer.removeHelpfulDuckFromHand();
                    pantryDiscard.add(ingredient);
                } else {
                    System.out.println("Layer not bakeable.");
                    return;
                }
            }
    
            currentPlayer.addToHand(layer);
            this.layers.remove(layer);
        } else {
            System.out.println("Layer not bakeable.");
        }
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

    public Collection<Layer> getBakeableLayers() {
        Collection<Layer> bakeableLayers = new ArrayList<>();
        for(Layer layer : layers) {
            int missingIngredients = 0;
            if(getCurrentPlayer().getHand().containsAll(layer.getRecipe())) {
                bakeableLayers.add(layer);
            } else {
                for(Ingredient ingredient : layer.getRecipe()) {
                    if(!getCurrentPlayer().getHand().contains(ingredient)) {
                        missingIngredients++;
                    }
                }
            }
            if(getCurrentPlayer().helpfulDuckCount() >= missingIngredients) {
                bakeableLayers.add(layer);
            }
        }
        return bakeableLayers;
    }

    public LinkedList<Player> getPlayers() {
        return new LinkedList<>(this.players);
    }

    public Player getCurrentPlayer() {
        return getPlayers().get(currentPlayerIndex);
    }

    public Collection<Layer> getLayers() {
        return layers;
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
        // Instantiate players list
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        Collections.shuffle((List) pantryDeck, random);
        customers = new Customers(customerDeckFile, random, layers, players.size());
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
