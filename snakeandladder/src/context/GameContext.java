package snakeandladder.src.context;

import java.util.ArrayList;
import java.util.List;

import snakeandladder.src.enums.GameState;
import snakeandladder.src.models.Board;
import snakeandladder.src.models.Dice;
import snakeandladder.src.models.Player;

public class GameContext {
    private final List<Player> players;
    private final Dice dice;
    private Player currentPlayer;
    private final Board board;
    private GameState gameState;
    private Player winner;

    public GameContext(Board board) {
        players = new ArrayList<>();
        dice = new Dice(6);
        this.board = board;
        this.currentPlayer = null;
        this.gameState = GameState.NEW;
        this.winner = null;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int throwDice() {
        return dice.roll();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Board getBoard() {
        return board;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean isGameOver() {
        return gameState == GameState.FINISHED;
    }

    public Player getWinner() {
        return winner;
    }
}
