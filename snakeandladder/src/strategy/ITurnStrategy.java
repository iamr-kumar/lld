package snakeandladder.src.strategy;

import java.util.List;

import snakeandladder.src.models.Player;

public interface ITurnStrategy {
    Player getNextPlayer(List<Player> players, Player currentPlayer, int diceValue);

    Player getFirstPlayer(List<Player> players);
    // String getStrategyName();
}
