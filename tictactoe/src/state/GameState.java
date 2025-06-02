package tictactoe.src.state;

import tictactoe.src.player.Player;

public interface GameState {
    void next(GameContext context, Player player, boolean hasWon);

    boolean isGameOver();

    GameOutcome getGameOutcome();
}