package bakery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MagicBakery {
    public Collection<Player> players;
    private int currentPlayerIndex;
    private int actionsUsed = 0;

    public enum ActionType {
        DRAW_INGREDIENT, PASS_INGREDIENT, BAKE_LAYER, FULFIL_ORDER, REFRESH_PANTRY
    }

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) {
        players = new LinkedList<Player>();
        currentPlayerIndex = 0;
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

    public void startGame(List<String> playerNames, String customerDeckFile) {
        
    }

}
