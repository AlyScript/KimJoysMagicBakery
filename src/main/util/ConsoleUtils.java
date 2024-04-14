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

public class ConsoleUtils {
    private Console console;

    public ConsoleUtils() {
        console = System.console();
        if (console == null) {
            throw new IllegalStateException("No console available");
        }
    }

    public String readLine() {
        return console.readLine();
    }

    public String readLine(String fmt, Object[] args) {
            return console.readLine(fmt, args);
        }

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

    public CustomerOrder promptForCustomer(String prompt, Collection<CustomerOrder> customers) {
        Collection<Object> customersAsObjects = new ArrayList<>(customers);
        return (CustomerOrder) promptEnumerateCollection(prompt, customersAsObjects);
    }

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

    public File promptForFilePath(String prompt) {
        System.out.println(prompt);
        String path = console.readLine();
        return new File(path);
    }

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

    public boolean promptForStartLoad(String prompt) {
        do {if(console.readLine().substring(0, 1).equalsIgnoreCase("S")) {
            return true;
        }} while(console.readLine().isEmpty());
        return false;
    }

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
