package tictactoe.src.state;

import tictactoe.src.player.Player;

public class OTurnState implements GameState {

    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        if (hasWon) {
            context.setState(new OWinState());
        } else {
            context.setState(new XTurnState());
        }
    }

    @Override
    public boolean isGameOver() {
        return false; // O's turn is not the end of the game
    }

    @Override
    public GameOutcome getGameOutcome() {
        return GameOutcome.IN_PROGRESS; // The game is still ongoing
    }

}
