package state;

import player.Player;

public class XWinState implements GameState {

    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        // Game is over, no further actions can be taken
        // This method can be left empty or throw an exception if called
    }

    @Override
    public boolean isGameOver() {
        return true;
    }

    @Override
    public GameOutcome getGameOutcome() {
        return GameOutcome.X_WIN; // The game outcome is X's win
    }

}
