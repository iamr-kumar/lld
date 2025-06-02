package tictactoe.src.state;

import tictactoe.src.player.Player;

public class DrawState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        // In a draw state, the game does not transition to another state
        // The game is over and remains in the draw state
    }

    @Override
    public boolean isGameOver() {
        return true; // The game is over in a draw state
    }

    @Override
    public GameOutcome getGameOutcome() {
        return GameOutcome.DRAW; // The game outcome is a draw
    }
}
