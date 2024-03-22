package util;
import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bakery.MagicBakery;
import bakery.Player;

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

    public String readLine(String fmt, Object args) {
        return console.readLine(fmt, args);
    }

    public Player promptForExistingPlayer(String prompt, MagicBakery bakery) {
        Player chosenPlayer = null;
        Player currentPlayer = bakery.getCurrentPlayer();
        while (chosenPlayer == null) {
            String input = console.readLine();
            for (Player player : bakery.getPlayers()) {
                if (player.toString().equalsIgnoreCase(input) && !player.equals(currentPlayer)) {
                    chosenPlayer = player;
                    break;
                }
            }
            if (chosenPlayer == null) {
                System.out.println("Player not found, please try again.");
            }
        }
        return chosenPlayer;
    }

    public File promptForFilePath(String prompt) {
        System.out.println(prompt);
        String path = console.readLine();
        return new File(path);
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

}
