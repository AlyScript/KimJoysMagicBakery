package bakery;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import util.*;

public class MagicBakery implements java.io.Serializable {
    private Customers customers;
    private Collection<Layer> layers;
    private Collection<Player> players;
    private Collection <Ingredient> pantry;
    private Collection<Ingredient> pantryDeck;
    private Collection<Ingredient> pantryDiscard;
    private Random random;
    private static final long serialVersionUID = 11085168;

    ConsoleUtils console;

    private int currentPlayerIndex;
    private int actionsUsed;

    public enum ActionType {
        DRAW_INGREDIENT, PASS_INGREDIENT, BAKE_LAYER, FULFIL_ORDER, REFRESH_PANTRY
    }

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) {
        layers = CardUtils.readLayerFile(layerDeckFile);
        players = new LinkedList<Player>();
        pantryDeck = new Stack<Ingredient>();
        pantryDeck.addAll(CardUtils.readIngredientFile(ingredientDeckFile));
        pantry = new Stack<>();
        pantryDiscard = new Stack<>();
        random = new Random(seed);

        console = new ConsoleUtils();

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
                pantry.add(drawFromPantryDeck());
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
        actionsUsed = 0;
        return true;
    }

    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish) {
        return null;
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

    public Collection<Player> getPlayers() {
        return this.players;
    }

    public Player getCurrentPlayer() {
        Player[] playerArray = players.toArray(new Player[0]);
        return playerArray[currentPlayerIndex];
    }

    public Customers getCustomers() {
        return this.customers;
    }

    public Collection<CustomerOrder> getFulfilableCustomers() {
        Collection<CustomerOrder> fulfilableCustomers = new ArrayList<>();
        for(CustomerOrder customerOrder : customers.getActiveCustomers()) {
            if(customerOrder.canFulfill(getCurrentPlayer().getHand())) {
                fulfilableCustomers.add(customerOrder);
            }
        }
        return fulfilableCustomers;
    }

    public Collection<CustomerOrder> getGarnishableCustomers() {
        Collection<CustomerOrder> garnishableCustomers = new ArrayList<>();
        for(CustomerOrder customerOrder : customers.getActiveCustomers()) {
            if(customerOrder.canGarnish(getCurrentPlayer().getHand())) {
                garnishableCustomers.add(customerOrder);
            }
        }
        return garnishableCustomers;
    }

    public Collection<Layer> getLayers() {
        return layers;
    }

    public Collection<Ingredient> getPantry() {
        return pantry;
    }

    public static MagicBakery loadState(File file) {
        return null;
    }

    public void passCard(Ingredient ingredient, Player recipient) {
        if (getActionsRemaining() > 0) {
            getCurrentPlayer().removeFromHand(ingredient);
            recipient.addToHand(ingredient);
            actionsUsed++;
        } else {
            System.out.println("No actions remaining.");
        }
    }

    public void printCustomerServiceRecord() {
        System.out.println("Customer service record:");
        for(Player player : players) {
            //System.out.printf("%s: %d\n", player.toString(), player.getCustomerServiceRecord());
        }
    }

    public void printGameState() {
        System.out.println("Game state:");
        System.out.printf("Current player: %s\n", getCurrentPlayer().toString());
        System.out.printf("Actions remaining: %d\n", getActionsRemaining());
        System.out.printf("Pantry: %s\n", pantry.toString());
        System.out.printf("Players: %s\n", players.toString());
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

    public void saveState(File file) {

    }

    public void startGame(List<String> playerNames, String customerDeckFile) {
        // Instantiate players list
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        customers = new Customers(customerDeckFile, random, layers, players.size());
        Collections.shuffle((List) pantryDeck, random);
        for(int i=0; i<5; i++) {
            pantry.add(drawFromPantryDeck());
        }
        // this is where i need to draw one or two customers
        // and add them to the customers list (q asked on discussion board why this isnt in UML)
        customers.drawCustomer();
        for(Player player : players) {
            for(int i=0; i<3; i++) {
                player.addToHand(drawFromPantryDeck());
            }
        }

        System.out.println("\nWelcome to Kim Joy's Magic Bakery!");

        while(customers.getActiveCustomers().size() > 0 || customers.getCustomerDeck().size() > 0) {
            Player currentPlayer = getCurrentPlayer();
            System.out.printf("\nCurrent player: %s\n", currentPlayer.toString());
            System.out.printf("\nPantry deck: %s\n", pantry.toString());
            System.out.println(displayOptions());

            int choice = Integer.parseInt(console.readLine("Enter your choice: (1-5)", null));
            switch(choice) {
                case 1:
                    String ingredientName = console.readLine("Enter the ingredient name: ", null);
                    drawFromPantry(ingredientName);
                    break;
                case 2:
                    Player targetPlayer = console.promptForExistingPlayer("Enter player number: ", this);
                    if (targetPlayer != null && !currentPlayer.getHand().isEmpty()) {
                        Ingredient ingredientToPass = currentPlayer.getHand().get(0); // or random selection
                        passCard(ingredientToPass, targetPlayer);
                        System.out.println("Current player's hand: " + currentPlayer.getHand());
                    } else if (currentPlayer.getHand().isEmpty()) {
                        System.out.println("Current player's hand is empty.");
                    }
                    break;
                case 3:
                    // if(console.promptForAction("Enter the name of the layer you want to bake: ").equals(ActionType.BAKE_LAYER)) {
                        
                    // }
                    // bakeLayer(layer);
                    break;
                case 4:
                    //CustomerOrder customer = console.promptForFulfillableOrder(this);
                    //fulfillOrder(customer, false);
                    break;
                case 5:
                    refreshPantry();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }

    }

    // added this method to display options to the player
    public String displayOptions() {
        String result = "";
        result+=("\nChoose from the options listed below: \n\n1. Draw an ingredient from the pantry \n2. Pass a card to another player \n3. Bake a layer \n4. Fulfill a customer order \n5. Refresh the pantry");
        return result;
    }

}
