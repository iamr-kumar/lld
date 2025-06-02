package tictactoe.src.state;

import tictactoe.src.player.Player;

public class XTurnState implements GameState {
    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        if (hasWon) {
            context.setState(new XWinState());
        } else {
            context.setState(new OTurnState());
        }
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public GameOutcome getGameOutcome() {
        return GameOutcome.IN_PROGRESS; // The game is still ongoing
    }
}
