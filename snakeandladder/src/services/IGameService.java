package snakeandladder.src.services;

import snakeandladder.src.models.Player;
import snakeandladder.src.models.Position;

public interface IGameService {
    public void addPlayer(Player player);

    public void startGame();

    public void endGame();

    public int throwDice();

    public Position movePlayer(Player player, int diceValue);

    public Player getWinner();

    public boolean isGameOver();

    public boolean playTurn();
}
