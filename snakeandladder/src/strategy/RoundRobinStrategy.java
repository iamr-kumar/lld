package snakeandladder.src.strategy;

import java.util.List;

import snakeandladder.src.models.Player;

public class RoundRobinStrategy implements ITurnStrategy {
    @Override
    public Player getNextPlayer(List<Player> players, Player currentPlayer, int diceValue) {
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
