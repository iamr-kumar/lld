package tictactoe.src.state;

import tictactoe.src.player.Player;

public class OWinState implements GameState {

    @Override
    public void next(GameContext context, Player player, boolean hasWon) {
        if (hasWon) {
            System.out.println("Player O has won the game!");
            context.setState(this);
        } else {
            System.out.println("It's Player X's turn now.");
            context.setState(new XTurnState());
        }
    }

    @Override
    public boolean isGameOver() {
        return true; // The game is over when O wins
    }

    @Override
    public GameOutcome getGameOutcome() {
        return GameOutcome.O_WIN; // The game outcome is O's win
    }

}
