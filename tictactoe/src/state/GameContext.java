package tictactoe.src.state;

import tictactoe.src.player.Player;

public class GameContext {
    private GameState gameState;

    public GameContext() {
        this.gameState = new XTurnState();
    }

    public void setState(GameState gameState) {
        this.gameState = gameState;
    }

    public void next(Player player, boolean hasWon, boolean isDraw) {
        if (isDraw) {
            this.gameState = new DrawState();
            return;
        }
        gameState.next(this, player, hasWon);
    }

    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    public GameState getGameState() {
        return gameState;
    }
}
