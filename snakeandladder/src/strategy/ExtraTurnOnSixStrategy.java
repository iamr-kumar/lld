package snakeandladder.src.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import snakeandladder.src.models.Player;

public class ExtraTurnOnSixStrategy implements ITurnStrategy {
    private final Map<Player, Integer> consecutiveSixCount;

    public ExtraTurnOnSixStrategy() {
        this.consecutiveSixCount = new HashMap<>();
    }

    @Override
    public Player getNextPlayer(List<Player> players, Player currentPlayer, int diceValue) {
        if (diceValue == 6) {
            int count = consecutiveSixCount.getOrDefault(currentPlayer, 0) + 1;
            // ideally we should cancel the turn after 3 consecutive sixes, but for
            // simplicity we are just moving to next player
            // cancelling would require undo operations which can be complex to implement
            if (count >= 3) {
                consecutiveSixCount.put(currentPlayer, 0); // Reset count after 3 consecutive sixes
                return selectNextPlayer(players, currentPlayer); // Move to next player
            }
            consecutiveSixCount.put(currentPlayer, count);
            return currentPlayer; // Player gets an extra turn
        } else {
            return selectNextPlayer(players, currentPlayer); // Move to next player
        }

    }

    private Player selectNextPlayer(List<Player> players, Player currentPlayer) {
        int currentIndex = players.indexOf(currentPlayer);
        if (currentIndex == -1) {
            throw new IllegalArgumentException("Current player not found in the list of players");
        }
        int nextIndex = (currentIndex + 1) % players.size();
        return players.get(nextIndex);
    }

    @Override
    public Player getFirstPlayer(List<Player> players) {
        return players.get(0);
    }
}
