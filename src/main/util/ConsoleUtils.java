package util;
import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import bakery.CustomerOrder;
import bakery.Ingredient;
import bakery.MagicBakery;
import bakery.Player;
import bakery.MagicBakery.ActionType;

/**
 * Provides utility functions for managing user interactions through the console within an application.
 * This class is designed to handle all console-based input and output operations, offering various methods to
 * facilitate user input validation, menu selections, and data retrieval through prompts.
 * It ensures robust handling of user interactions in environments where a console is available.
 * Note that it may not function as intended in some development environments lacking standard console support.
 *
 * Usage of this class includes reading user inputs, displaying menus, and prompting for file paths, ensuring
 * that each interaction is user-friendly and clear for application users.
 *
 * @author Adam Aly
 * @version 1.0
 * @since 2024
 */
public class ConsoleUtils {
    private Console console;

    /**
     * Constructs a new ConsoleUtils object and initializes the console for user interaction.
     * If the console is not available (e.g., when running in some IDE environments), an IllegalStateException is thrown.
     *
     */
    public ConsoleUtils() {
        console = System.console();
        if (console == null) {
            throw new IllegalStateException("No console available");
        }
    }

    /**
     * Reads a single line of text from the console.
     * This method waits for the user to enter a line of input and returns it as a string.
     *
     * @return The line of text entered by the user.
     */
    public String readLine() {
        return console.readLine();
    }

    /**
     * Formats and displays a prompt to the user, then reads a single line of input from the console.
     * This method uses {@link java.io.Console#format(String, Object...)} to format the prompt before displaying it.
     *
     * @param fmt A format string as described in {@link java.util.Formatter} with optional arguments.
     * @param args Arguments referenced by the format specifiers in the format string.
     * @return The line of text entered by the user.
     */
    public String readLine(String fmt, Object[] args) {
            return console.readLine(fmt, args);
        }

    /**
     * Prompts the user to choose an action from a list of available actions in the bakery.
     * This method displays each available action and expects the user to enter a selection corresponding to one of them.
     *
     * @param prompt The message to display to the user before listing actions.
     * @param bakery The MagicBakery instance from which to retrieve available actions.
     * @return The ActionType chosen by the user.
     */
    public ActionType promptForAction(String prompt, MagicBakery bakery) {
        //System.out.println(prompt);
        ActionType result = (ActionType) promptEnumerateCollection(prompt, bakery.getAvailableActions());

        // do {
        //     input = console.readLine();
        //     if (input.equalsIgnoreCase("D")) {
        //         return ActionType.DRAW_INGREDIENT;
        //     } else if (input.equalsIgnoreCase("P")) {
        //         return ActionType.PASS_INGREDIENT;
        //     } else if (input.equalsIgnoreCase("B")) {
        //         return ActionType.BAKE_LAYER;
        //     } else if (input.equalsIgnoreCase("F")) {
        //         return ActionType.FULFIL_ORDER;
        //     } else if (input.equalsIgnoreCase("R")) {
        //         return ActionType.REFRESH_PANTRY;
        //     } else {
        //         System.out.println("Invalid input. Please enter a valid action.");
        //     }
        // } while (true);
        return result;
    }

    /**
     * Prompts the user to select a customer from a given collection of customer orders.
     * The method lists each customer order and expects the user to choose one by entering the corresponding number.
     *
     * @param prompt The message to display to the user before listing customer orders.
     * @param customers A collection of CustomerOrder objects to choose from.
     * @return The CustomerOrder selected by the user.
     */
    public CustomerOrder promptForCustomer(String prompt, Collection<CustomerOrder> customers) {
        Collection<Object> customersAsObjects = new ArrayList<>(customers);
        return (CustomerOrder) promptEnumerateCollection(prompt, customersAsObjects);
    }

    /**
     * Prompts the user to select an existing player from a list of players.
     * This method displays all players in the provided collection and allows the user to select one by entering the corresponding index.
     *
     * @param prompt The prompt message shown before listing players.
     * @param bakery A collection of Player objects from which the user can choose.
     * @return The Player object selected by the user.
     */
    public Player promptForExistingPlayer(String prompt, MagicBakery bakery) {
        Player currentPlayer = bakery.getCurrentPlayer();
        Collection<Object> otherPlayers = bakery.getPlayers()
        .stream().filter(player -> !player.equals(currentPlayer))
        .collect(Collectors.toList());

        Player chosenPlayer = (Player) promptEnumerateCollection(prompt, otherPlayers);
        if (chosenPlayer == null) {
            System.out.println("Player not found, please try again.");
        }

        return chosenPlayer;
    }

    /**
     * Prompts the user to enter a file path and returns a File object corresponding to the input.
     * The method continuously prompts the user until a valid file path is entered and confirms the existence of the file.
     *
     * @param prompt The message to display to the user, instructing them to input a file path.
     * @return A File object representing the file path entered by the user.
     */
    public File promptForFilePath(String prompt) {
        System.out.println(prompt);
        String path = console.readLine();
        return new File(path);
    }

    /**
     * Prompts the user to select an ingredient from a list of available ingredients.
     * Displays a list of ingredients based on the provided collection and asks the user to choose one by entering the corresponding number.
     *
     * @param prompt The message displayed to the user to guide their selection.
     * @param ingredients A collection of Ingredient objects available for selection.
     * @return The Ingredient selected by the user.
     */
    public Ingredient promptForIngredient(String prompt, Collection<Ingredient> ingredients) {
        Ingredient chosenIngredient = null;
        Collection<Object> ingredientsAsObjects = new ArrayList<>(ingredients);

        while (chosenIngredient == null) {
            chosenIngredient = (Ingredient) promptEnumerateCollection(prompt, ingredientsAsObjects);
            if (chosenIngredient == null) {
                System.out.println("Ingredient not found, please try again.");
            }
        }

        return chosenIngredient;
    }

    /**
     * Prompts the user to enter names for new players and collects them into a list.
     * The user can enter multiple names, with each name added to the list upon pressing Enter.
     *
     * @param prompt The prompt message instructing the user on how to input player names.
     * @return A list of strings, each representing a player's name.
     */
    public List<String> promptForNewPlayers(String prompt) {
        ArrayList<String> result = new ArrayList<>();
        String input;
        do {
            System.out.println(prompt);
            input = console.readLine();
            if (!input.isEmpty()) {
                result.add(input);
            }
        } while(result.size() < 2 || (result.size() < 5 && !input.isEmpty()));
        return result;
    }

    /**
     * Prompts the user to start loading with a specific command.
     * This method continues to prompt the user until they enter a command that begins with "S".
     *
     * @param prompt The instruction to the user indicating how to proceed to start loading.
     * @return true if the user enters a command starting with "S", otherwise false.
     */
    public boolean promptForStartLoad(String prompt) {
        do {if(console.readLine().substring(0, 1).equalsIgnoreCase("S")) {
            return true;
        }} while(console.readLine().isEmpty());
        return false;
    }

    /**
     * Prompts the user with a yes or no question and returns the user's response as a boolean.
     * The method repeatedly prompts the user until a valid input ("Y" or "N") is received.
     *
     * @param prompt The question to display to the user.
     * @return true if the user responds with "Y", false if the user responds with "N".
     */
    public boolean promptForYesNo(String prompt) {
        String input;
        do {
            System.out.println(prompt);
            input = console.readLine();
            if (input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("N")) {
                break;
            }
        } while(true);
        return input.equalsIgnoreCase("Y");
    }

    /**
     * Displays a list of items from the provided collection and prompts the user to make a selection.
     * Each item in the collection is displayed with a number, and the user is expected to enter the number corresponding to their choice.
     * This method handles user input errors such as non-numeric input or out-of-range selections by re-prompting the user.
     *
     * @param prompt The message to display before listing the items.
     * @param collection A collection of objects from which the user can make a selection.
     * @return The object selected by the user from the collection, or null if the collection is empty.
     * @throws IllegalArgumentException if the collection is null or empty.
     */
    private Object promptEnumerateCollection(String prompt, Collection<Object> collection) throws IllegalArgumentException {
        if(collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException();
        }
        System.out.printf("\n%s\n", prompt);
        int selection;
        if (collection.isEmpty()) {
            System.out.println("The collection is empty.");
            return null;
        }
    
        int i = 1;
        for (Object item : collection) {
            System.out.printf("[%d]. %s\n", i, item.toString());
            i++;
        }
    
        do {
            try {
                selection = Integer.parseInt(console.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
    
            if (selection < 1 || selection > collection.size()) {
                System.out.println("Invalid selection. Please select a number between 1 and " + collection.size() + ".");
            } else {
                return new ArrayList<>(collection).get(selection - 1);
            }
        } while (true);
    }

}
