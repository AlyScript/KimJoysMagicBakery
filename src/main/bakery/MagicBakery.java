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

/**
 * Represents the central management class for a bakery-themed game, coordinating the actions of players,
 * managing ingredient inventories, and processing customer orders. This class is responsible for initializing the game,
 * distributing ingredients, and handling player actions such as drawing ingredients, passing them, baking layers,
 * fulfilling orders, and refreshing the pantry.
 * 
 * The class utilizes various collections to manage layers, players, ingredients, and customer orders, ensuring that all game
 * elements interact cohesively. It also tracks the current player and the number of actions used to enforce game rules regarding
 * turn-based play and action limits.
 *
 * @author Adam Aly
 * @version 1.0
 * @since 2024
 */
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

    /**
     * Defines the types of actions that players can perform during their turn in the game.
     * Each action type represents a different possible player interaction such as drawing ingredients,
     * passing ingredients to other players, baking layers, fulfilling orders, and refreshing the pantry.
     */
    public enum ActionType {
        DRAW_INGREDIENT, PASS_INGREDIENT, BAKE_LAYER, FULFIL_ORDER, REFRESH_PANTRY
    }

    /**
     * Initializes a new MagicBakery game with specified seed values and deck files for ingredients and layers.
     * This constructor sets up the game environment by loading layers and ingredients from specified files,
     * initializing player settings, and preparing the initial state of the pantry and player hands.
     *
     * @param seed the seed value used for randomizing game elements, ensuring varied game play.
     * @param ingredientDeckFile the file path for the ingredient deck, used to populate the game's ingredient stock.
     * @param layerDeckFile the file path for the layer deck, used to define available layers for players to bake.
     * @throws FileNotFoundException if the specified deck files cannot be found, preventing game initialization.
     */
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

    /**
     * Attempts to bake a specified layer using ingredients from the current player's hand. This method checks if the player
     * has sufficient actions remaining and the necessary ingredients, including the use of helpful ducks as substitutes.
     * If successful, the layer is removed from the player's hand and added to their completed items.
     *
     * @param layer the layer to be baked, must not be null and must be bakeable according to the game rules.
     * @throws TooManyActionsException if the player has no actions remaining.
     * @throws WrongIngredientsException if the necessary ingredients are not present in the player's hand.
     */
    public void bakeLayer(Layer layer) throws TooManyActionsException, WrongIngredientsException {
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

    /**
     * Draws a single ingredient from the pantry deck. If the pantry deck is empty, the pantry is restored from the discard pile.
     * If both the pantry and discard pile are empty, an EmptyPantryException is thrown.
     *
     * @return the drawn ingredient from the pantry deck.
     * @throws EmptyPantryException if both the pantry deck and discard pile are empty.
     */
    private Ingredient drawFromPantryDeck() throws EmptyPantryException {
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

    /**
     * Draws an ingredient by name from the pantry to the current player's hand. If the named ingredient is found,
     * it is moved to the player's hand and replaced in the pantry by drawing from the pantry deck.
     *
     * @param ingredientName the name of the ingredient to draw from the pantry.
     * @throws TooManyActionsException if no actions are remaining for the current player.
     * @throws WrongIngredientsException if the ingredient is not found in the pantry.
     */
    public void drawFromPantry(String ingredientName) throws TooManyActionsException, WrongIngredientsException {
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

     /**
     * Draws an ingredient by name from the pantry to the current player's hand. If the named ingredient is found,
     * it is moved to the player's hand and replaced in the pantry by drawing from the pantry deck.
     *
     * @param ingredient the name of the ingredient to draw from the pantry.
     * @throws TooManyActionsException if no actions are remaining for the current player.
     * @throws WrongIngredientsException if the ingredient is not found in the pantry.
     */
    public void drawFromPantry(Ingredient ingredient) throws TooManyActionsException, WrongIngredientsException {
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
     * Ends the current player's turn and advances to the next player. If the round completes and returns to the first player,
     * additional game logic to process customer orders or other events may be executed.
     *
     * @return true if the turn successfully ends and transitions to the next player, false if conditions prevent ending the turn.
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

    /**
     * Attempts to fulfill a customer order using the current player's hand, optionally including garnishing. This method
     * checks for sufficient actions and required ingredients before proceeding.
     *
     * @param customer the customer order to be fulfilled.
     * @param garnish a boolean indicating whether to garnish the order if possible.
     * @return a list of ingredients used in fulfilling (and potentially garnishing) the order.
     * @throws TooManyActionsException if the player has no actions remaining to perform this task.
     */
    public List<Ingredient> fulfillOrder(CustomerOrder customer, boolean garnish) throws TooManyActionsException {
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

    /**
     * Determines the number of actions a player is permitted to take in their turn based on the number of players in the game.
     * Fewer players allow for more actions per player to maintain game balance.
     *
     * @return the maximum number of actions a player can take in a turn.
     */
    public int getActionsPermitted() {
        if(players.size() >= 4) {
            return 2;
        }
        return 3;
    }

    /**
     * Retrieves the number of actions remaining for the current player for this turn.
     *
     * @return the number of remaining actions.
     */
    public int getActionsRemaining() {
        return getActionsPermitted() - actionsUsed;
    }

    /**
     * Retrieves a collection of layers that the current player can potentially bake based on the ingredients in their hand.
     * This method calculates which layers are feasible to bake by checking the player's current ingredient inventory.
     *
     * @return a collection of bakeable layers based on the current player's ingredients.
     */
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

    /**
     * Retrieves the collection of players currently participating in the game.
     *
     * @return a collection of players in the game.
     */
    public Collection<Player> getPlayers() {
        return this.players;
    }

    /**
     * Retrieves the current player based on the game's turn order.
     *
     * @return the currently active player.
     */
    public Player getCurrentPlayer() {
        Player[] playerArray = players.toArray(new Player[0]);
        return playerArray[currentPlayerIndex];
    }

    /**
     * Retrieves the customers object which manages all customer orders in the game.
     *
     * @return the customers object containing all active and inactive customer orders.
     */
    public Customers getCustomers() {
        return this.customers;
    }

    /**
     * Identifies and returns a collection of customer orders that can be fulfilled based on the current player's hand.
     *
     * @return a collection of fulfilable customer orders.
     */
    public Collection<CustomerOrder> getFulfilableCustomers() {
        Collection<CustomerOrder> fulfilableCustomers = new ArrayList<>();
        for(CustomerOrder customerOrder : customers.getActiveCustomers()) {
            if(customerOrder != null && customerOrder.canFulfill(getCurrentPlayer().getHand())) {
                fulfilableCustomers.add(customerOrder);
            }
        }
        return fulfilableCustomers;
    }

    /**
     * Identifies and returns a collection of customer orders that can be garnished based on the remaining ingredients in the
     * current player's hand after fulfilling their main requirements.
     *
     * @return a collection of garnishable customer orders.
     */
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

    /**
     * Retrieves a sorted collection of all bakeable layers available in the game. The sorting ensures that the layers
     * are presented in a consistent order, typically based on some attribute such as name or difficulty.
     *
     * @return a sorted collection of layers.
     */
    public Collection<Layer> getLayers() {
        Set<Layer> layers = new HashSet<>(this.layers);
        ArrayList<Layer> result = new ArrayList<>(layers);
        Collections.sort(result);
        return result;
    }

    /**
     * Retrieves the collection of ingredients currently available in the pantry.
     *
     * @return a collection of ingredients available for players to draw.
     */
    public Collection<Ingredient> getPantry() {
        return pantry;
    }

    /**
 * Allows the current player to pass a specified ingredient to another player. This action is counted against
    * the current player's available actions.
    *
    * @param ingredient the ingredient to be passed to another player.
    * @param recipient the player who will receive the ingredient.
    * @throws TooManyActionsException if the current player has no actions remaining.
    * @throws WrongIngredientsException if the current player does not have the specified ingredient.
    */
    public void passCard(Ingredient ingredient, Player recipient) throws TooManyActionsException, WrongIngredientsException {
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

    /**
     * Prints a summary of customer service records, including the number of customers served, garnished, and those who left.
     * This method is used for reporting and game analysis purposes.
     */
    public void printCustomerServiceRecord() {
        int garnished = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GARNISHED).size();
        int fulfilled = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.FULFILLED).size() + garnished;
        int left = customers.getInactiveCustomersWithStatus(CustomerOrderStatus.GIVEN_UP).size();
        System.out.printf("\nHappy customers eating baked goods: %d (%d Garnished) \nGone to greggs instead : %d\n", fulfilled, garnished, left);
    }

    /**
     * Prints the current state of the game, including the list of available layers, ingredients in the pantry, and the status
     * of customer orders. This method provides a comprehensive view of the game's progress and is crucial for player decision-making.
     */
    public void printGameState() {
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

    /**
     * Refreshes the pantry by shuffling all discarded ingredients back into the pantry deck. This action is counted against
     * the current player's available actions.
     *
     * @throws TooManyActionsException if no actions are remaining for the current player to perform this task.
     */
    public void refreshPantry() throws TooManyActionsException {
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

    /**
     * Saves the current game state to a file. This method serializes the entire game environment allowing the game
     * to be paused and resumed at a later time.
     *
     * @param file the file to which the game state will be saved.
     * @throws IOException if there is an error writing to the file.
     */
    public void saveState(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    /**
     * Loads a previously saved game state from a file. This method is used to resume a game from a specific point,
     * restoring all relevant game data including player positions, pantry contents, and customer orders.
     *
     * @param file the file from which to load the game state.
     * @return a MagicBakery instance representing the loaded game state.
     * @throws IOException if there is an error reading from the file.
     * @throws ClassNotFoundException if the serialized class is not found.
     */
    public static MagicBakery loadState(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (MagicBakery) ois.readObject();
        }
    }

    /**
     * Initializes and starts a new game session with the given player names and customer deck file. This method sets up
     * players, loads customers, and prepares initial game settings.
     *
     * @param playerNames a list of names for players participating in the game.
     * @param customerDeckFile the file path to load customer orders from.
     * @throws FileNotFoundException if the customer deck file cannot be found.
     * @throws IllegalArgumentException if the number of players is not between 2 and 5.
     */
    public void startGame(List<String> playerNames, String customerDeckFile) throws FileNotFoundException, IllegalArgumentException {
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
    /**
     * Retrieves a collection of actions that are currently available to the active player based on the game state.
     * This method assesses the player's situation, including remaining actions and game conditions, to determine which actions
     * the player can perform next. It dynamically compiles a list of possible actions such as drawing ingredients,
     * passing them to another player, baking a layer, fulfilling an order, or refreshing the pantry, contingent upon the
     * feasibility of each action at the moment of invocation.
     *
     * @return a collection of {@link ActionType} representing the actions available to the current player. If no actions
     * are possible due to game rules or conditions, this collection will be empty.
     */
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
