// import bakery.*;
// import util.ConsoleUtils;

// import java.io.FileNotFoundException;
// import java.util.List;

// public class BakeryDriver {
//     private ConsoleUtils consoleUtils = new ConsoleUtils();

//     public BakeryDriver() throws FileNotFoundException {
//         consoleUtils = new ConsoleUtils();
//         MagicBakery magicBakery = new MagicBakery(0, "io/ingredients.csv", "io/layers.csv");
//         List<String> playerNames = consoleUtils.promptForNewPlayers("Enter player name: ");
//         magicBakery.startGame(playerNames, "io/customers.csv");
//         System.out.println(magicBakery.getCustomers().getCustomerDeck());

//         // int i = 0;
//         // while (i < 20) {
//         //     Player currentPlayer = magicBakery.getCurrentPlayer();
//         //     System.out.println("Current player: " + currentPlayer.toString());

//         //     System.out.println("Enter a player to pass an ingredient to: ");
//         //     Player targetPlayer = consoleUtils.promptForExistingPlayer("Enter player name: ", magicBakery);
//         //     if (targetPlayer != null && !currentPlayer.getHand().isEmpty()) {
//         //         Ingredient ingredientToPass = currentPlayer.getHand().get(0); // or random selection
//         //         magicBakery.passCard(ingredientToPass, targetPlayer);
//         //         System.out.println("Current player's hand: " + currentPlayer.getHand());
//         //     } else if (currentPlayer.getHand().isEmpty()) {
//         //         System.out.println("Current player's hand is empty.");
//         //     }

//         //     if (magicBakery.endTurn()) {
//         //         i++;
//         //     }
//         // }
//     }

//     public static void main(String[] args) throws FileNotFoundException {
//         new BakeryDriver();
//     }

// }