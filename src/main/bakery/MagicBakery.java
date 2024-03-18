package bakery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class MagicBakery {
    public Collection<Player> players;
    private int currentPlayerIndex;

    public MagicBakery(long seed, String ingredientDeckFile, String layerDeckFile) {
        players = new LinkedList<Player>();
        currentPlayerIndex = 0;
    }

    public boolean endTurn() {
        if(players.isEmpty()) {
            return false;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        return true;
    }

    public int getActionsPermitted() {
        if(players.size() >= 4) {
            return 2;
        }
        return 3;
    }

    public int getActionsRemaining() {
        return getActionsPermitted() - 1;
    }

    public void startGame(ArrayList<String> playerNames, String customerDeckFile) {
        
    }

    public LinkedList<Player> getPlayers() {
        return new LinkedList<>(this.players);
    }

    public Player getCurrentPlayer() {
        return getPlayers().get(currentPlayerIndex);
    }

}
