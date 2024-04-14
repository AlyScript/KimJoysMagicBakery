package bakery;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.io.*;

import bakery.CustomerOrder.CustomerOrderStatus;
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

    private int currentPlayerIndex;
    private int actionsUsed;

    public enum ActionType {
        DRAW_INGREDIENT, PASS_INGREDIENT, BAKE_LAYER, FULFIL_ORDER, REFRESH_PANTRY
    }

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) throws FileNotFoundException {
        try {
            layers = CardUtils.readLayerFile(layerDeckFile);
        } catch (Exception e) {
            throw new FileNotFoundException("Layer deck file not found.");
        }
        players = new LinkedList<Player>();
        pantryDeck = new Stack<Ingredient>();
        try {
            pantryDeck.addAll(CardUtils.readIngredientFile(ingredientDeckFile));
        } catch (Exception e) {
            throw new FileNotFoundException("Ingredient deck file not found.");
        }
        pantry = new Stack<>();
        pantryDiscard = new Stack<>();
        random = new Random(seed);

        currentPlayerIndex = 0;
        actionsUsed = 0;
    }

    public void bakeLayer(Layer layer) {
        if(getActionsRemaining() <= 0) {
            throw new TooManyActionsException();
        }
        if(getBakeableLayers().contains(layer)) {
            Player currentPlayer = getCurrentPlayer();
            List<Ingredient> recipe = new ArrayList<>(layer.getRecipe());
    
            for (Ingredient ingredient : recipe) {
                if (currentPlayer.getHand().contains(ingredient)) {
                    currentPlayer.removeFromHand(ingredient);
                    pantryDiscard.add(ingredient);
                } else if (currentPlayer.helpfulDuckCount() >= 1 && !(ingredient instanceof Layer)) {
                    pantryDiscard.add(currentPlayer.removeHelpfulDuckFromHand());
                } else {
                    throw new WrongIngredientsException("Incorrect ingredients to bake this layer.");
                }
            }
            currentPlayer.addToHand(layer);
            this.layers.remove(layer);
            actionsUsed++;
        } else {
            throw new WrongIngredientsException("Layer not bakeable.");
        }
    }

    private Ingredient drawFromPantryDeck() {
        if (pantryDeck.isEmpty()) {
            if(pantryDiscard.isEmpty()) {
                throw new EmptyPantryException("Both pantry and discard pile are empty.", null);
            }
            restorePantry();
            //throw new EmptyPantryException(null, null);
        }
        Ingredient ingredient = ((Stack<Ingredient>) pantryDeck).pop();
        return ingredient;
    }

    public void drawFromPantry(String ingredientName) {
        if(getActionsRemaining() <= 0) {
            throw new TooManyActionsException();
        }
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
            throw new WrongIngredientsException(null);
        }
        actionsUsed++;
    }

    public void drawFromPantry(Ingredient ingredient) {
        if(getActionsRemaining() <= 0) {
            throw new TooManyActionsException();
        }
        if(pantry.contains(ingredient)) {
            getCurrentPlayer().addToHand(ingredient);
            pantry.remove(ingredient);
            pantry.add(drawFromPantryDeck());
        } else {
            throw new WrongIngredientsException(null);
        }
        actionsUsed++;
    }

    /**
     * Ends the current player's turn and advances to the next player.
     * @return true if the turn was successfully ended, false otherwise
     */
    public boolean endTurn() {
        // if (getActionsRemaining() > 0) {
        //     System.out.println("You still have actions remaining.");
        //     return false;
        // }
    
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    
        if (currentPlayerIndex == 0) {
            if(customers.getCustomerDeck().isEmpty()) {
                customers.timePasses();
                return false;
            } else {
                System.out.println("New round");
                customers.addCustomerOrder();
            }
        }
        actionsUsed = 0;
        return true;
    }

    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish) {
        List<Ingredient> usedIngredients = new ArrayList<>();
        List<Ingredient> drawnIngredients = new ArrayList<>();
        if(getActionsRemaining() <= 0) {
            throw new TooManyActionsException();
        }
        Player currentPlayer = getCurrentPlayer();
        usedIngredients = customer.fulfill(currentPlayer.getHand(), garnish);
        for(Ingredient ingredient : usedIngredients) {
            currentPlayer.removeFromHand(ingredient);
            if(!(layers.contains(ingredient))) {
                pantryDiscard.add(ingredient);
            } else {
                layers.add((Layer) ingredient);
            }
        }
        actionsUsed++;
        if(garnish) {
            Ingredient ingredient1 = drawFromPantryDeck();
            Ingredient ingredient2 = drawFromPantryDeck();
            currentPlayer.addToHand(ingredient1);
            currentPlayer.addToHand(ingredient2);
            drawnIngredients.add(ingredient1);
            drawnIngredients.add(ingredient2);
        }
        customers.remove(customer);
        if(!customers.customerWillLeaveSoon() && customers.peek() != null) {
            customers.peek().setStatus(CustomerOrderStatus.WAITING);
        }
        return drawnIngredients;
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
                if(!bakeableLayers.contains(layer)) {
                    bakeableLayers.add(layer);
                }
            } else {
                for(Ingredient ingredient : layer.getRecipe()) {
                    if(!getCurrentPlayer().getHand().contains(ingredient)) {
                        missingIngredients++;
                    }
                }
            }
            if(getCurrentPlayer().helpfulDuckCount() >= missingIngredients) {
                if(!bakeableLayers.contains(layer)) {
                    bakeableLayers.add(layer);
                }
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
            if(customerOrder != null && customerOrder.canFulfill(getCurrentPlayer().getHand())) {
                fulfilableCustomers.add(customerOrder);
            }
        }
        return fulfilableCustomers;
    }

    public Collection<CustomerOrder> getGarnishableCustomers() {
        Collection<CustomerOrder> garnishableCustomers = new ArrayList<>();
        ArrayList<Ingredient> availableIngredients = new ArrayList<>(getCurrentPlayer().getHand());
        for(CustomerOrder customer : customers.getActiveCustomers()) {
            if(customer.canFulfill(availableIngredients)) {
                availableIngredients.removeAll(customer.getRecipe());
            }
        }
        for(CustomerOrder customerOrder : customers.getActiveCustomers()) {
            if(customerOrder.canGarnish(availableIngredients)) {
                garnishableCustomers.add(customerOrder);
            }
        }
        return garnishableCustomers;
    }

    public Collection<Layer> getLayers() {
        Set<Layer> layers = new HashSet<>(this.layers);
        ArrayList<Layer> result = new ArrayList<>(layers);
        Collections.sort(result);
        return result;
    }

    public Collection<Ingredient> getPantry() {
        return pantry;
    }

    public void passCard(Ingredient ingredient, Player recipient) {
        if (getActionsRemaining() > 0) {
            if(getCurrentPlayer().getHand().contains(ingredient)) {
                getCurrentPlayer().removeFromHand(ingredient);
                recipient.addToHand(ingredient);
                actionsUsed++;
            } else {
                throw new WrongIngredientsException("Player does not have the ingredient to pass.");
            }
        } else {
            throw new TooManyActionsException();
        }
    }

    public void printCustomerServiceRecord() {
        int garnished = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).size();
        int fulfilled = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).size() + garnished;
        int left = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).size();
        System.out.printf("\nHappy customers eating baked goods: %d (%d Garnished) \nGone to greggs instead : %d\n", fulfilled, garnished, left);
    }

    public void printGameState() {
        //System.out.println("-----------------------------");
        
        System.out.printf("Layers:\n  %s\n", StringUtils.layersToStrings(getLayers()));
        System.out.printf("Pantry\n  %s\n", StringUtils.ingredientsToStrings(getPantry()));
        if(customers.size() > 0)System.out.printf("Waiting for service:\n  %s\n", StringUtils.customerOrdersToStrings(customers.getActiveCustomers()));
        else System.out.println("No customers waiting -- time for a brew :).");
        printCustomerServiceRecord();
        System.out.printf("\n%s it's your turn. Your hand contains: %s", getCurrentPlayer().toString(), getCurrentPlayer().getHandStr());
        // System.out.println(StringUtils.customerOrdersToStrings(customers.getActiveCustomers()));
        // System.out.printf("Current player: %s\n", getCurrentPlayer().toString());
        // System.out.printf("%s, your hand contains: %s\n", getCurrentPlayer().toString(), getCurrentPlayer().getHand().toString());
        // System.out.printf("Actions remaining: %d\n", getActionsRemaining());
        // System.out.printf("Pantry: %s\n", pantry.toString());
        // System.out.printf("Layers available to bake: %s\n", getBakeableLayers().toString());
        // System.out.printf("Players: %s\n", players.toString());
        // System.out.printf("%d Customer(s): %s\n", customers.size(), customers.getActiveCustomers().toString());
        // System.out.println("-----------------------------");
    }

    public void refreshPantry() {
        if(getActionsRemaining() <= 0) {
            throw new TooManyActionsException();
        }
        pantryDeck.addAll(pantryDiscard);
        pantryDiscard.addAll(pantry);
        pantry.clear();
        Collections.shuffle((List<Ingredient>) pantryDeck, random);
        for(int i=0; i<5; i++) {
            pantry.add(drawFromPantryDeck());
        }
        actionsUsed++;
    }

    public void saveState(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    public static MagicBakery loadState(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (MagicBakery) ois.readObject();
        }
    }

    public void startGame(List<String> playerNames, String customerDeckFile) throws FileNotFoundException {
        // Instantiate players list
        for (String name : playerNames) {
            players.add(new Player(name));
        }
        if(players.size() < 2 || players.size() > 5) {
            throw new IllegalArgumentException("Number of players must be between 2 and 5.");
        }
        try {
            customers = new Customers(customerDeckFile, random, layers, players.size());
        } catch (Exception e) {
            throw new FileNotFoundException("Customer deck file not found.");
        }
        Collections.shuffle((List<Ingredient>) pantryDeck, random);
        for(int i=0; i<5; i++) {
            pantry.add(drawFromPantryDeck());
        }
        if(players.size() == 2 || players.size() == 4) {
            customers.addCustomerOrder();
        } else {
            customers.addCustomerOrder();
            customers.addCustomerOrder();
        }

        for(Player player : players) {
            for(int i=0; i<3; i++) {
                player.addToHand(drawFromPantryDeck());
            }
        }

        // System.out.println("\nWelcome to Kim Joy's Magic Bakery!");

        // while(customers.size() > 0 || customers.getCustomerDeck().size() > 0) {
        
        //     while(getActionsRemaining() > 0) {
        //         Player currentPlayer = getCurrentPlayer();
        //         printGameState();
        //         ActionType choice = new ConsoleUtils().promptForAction("Choose an option number from the list below:", this);
        //         switch(choice) {
        //             case DRAW_INGREDIENT:
        //                 String ingredientName = new ConsoleUtils().readLine("Enter the ingredient name: ", null);
        //                 drawFromPantry(ingredientName);
        //                 break;
        //             case PASS_INGREDIENT:
        //                 Player targetPlayer = new ConsoleUtils().promptForExistingPlayer("Enter player number: ", this);
        //                 if (targetPlayer != null && !currentPlayer.getHand().isEmpty()) {
        //                     Ingredient ingredientToPass = currentPlayer.getHand().get(0); // or random selection
        //                     passCard(ingredientToPass, targetPlayer);
        //                     System.out.println("Current player's hand: " + currentPlayer.getHand());
        //                 } else if (currentPlayer.getHand().isEmpty()) {
        //                     System.out.println("Current player's hand is empty.");
        //                 }
        //                 break;
        //             case BAKE_LAYER:
        //                 String layerName = new ConsoleUtils().readLine("Enter the layer name: ", null);
        //                 for(Layer layer : layers) {
        //                     if(layer.toString().equalsIgnoreCase(layerName)) {
        //                         bakeLayer(layer);
        //                         break;
        //                     }
        //                 }
        //                 break;
        //             case FULFIL_ORDER:
        //                 boolean willGarnish = false;
        //                 CustomerOrder customerOrderToFulfil = new ConsoleUtils().promptForCustomer("Choose a customer to fulfil: ", getFulfilableCustomers());
        //                 if((customerOrderToFulfil.getGarnish().size() > 0) && customerOrderToFulfil.canGarnish(getCurrentPlayer().getHand())) {
        //                     willGarnish = new ConsoleUtils().promptForYesNo("Would you like to garnish the order? (Y/N)");
        //                 }
        //                 List<Ingredient> usedIngredients = customerOrderToFulfil.fulfill(getCurrentPlayer().getHand(), willGarnish);
        //                 System.out.printf("\n%s fulfilled the order [%s] with ingredients: %s\n", currentPlayer.toString(), customerOrderToFulfil.toString(), usedIngredients.toString());
        //                 break;
        //             case REFRESH_PANTRY:
        //                 refreshPantry();
        //                 break;
        //         }
        //     }
        
        //     if (!endTurn()) {
        //         break;
        //     }
        // }

    }

    /**
     * Restores the pantry by moving all ingredients from the discard pile back to the pantry.
     * Shuffles the pantry after restoring.
     */
    public void restorePantry() {
        pantryDeck.addAll(pantryDiscard);
        pantryDiscard.clear();
        Collections.shuffle((Stack<Ingredient>) pantryDeck, random);
        while(pantryDeck.size() <= 5) {
            pantry.add(drawFromPantryDeck());
        }
    }

    public Collection<Object> getAvailableActions() {
        ArrayList<Object> availableActions = new ArrayList<>();
        if (getActionsRemaining() > 0) {
            availableActions.add(ActionType.DRAW_INGREDIENT);
            if (!getCurrentPlayer().getHand().isEmpty()) {
                availableActions.add(ActionType.PASS_INGREDIENT);
            }
            if (!getBakeableLayers().isEmpty()) {
                availableActions.add(ActionType.BAKE_LAYER);
            }
            if (!getFulfilableCustomers().isEmpty()) {
                availableActions.add(ActionType.FULFIL_ORDER);
            }
            availableActions.add(ActionType.REFRESH_PANTRY);
        }
        return availableActions;
    }

}
