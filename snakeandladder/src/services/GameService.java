package snakeandladder.src.services;

import snakeandladder.src.context.GameContext;
import snakeandladder.src.enums.GameState;
import snakeandladder.src.models.Board;
import snakeandladder.src.models.Cell;
import snakeandladder.src.models.Player;
import snakeandladder.src.models.Position;
import snakeandladder.src.strategy.ITurnStrategy;

public class GameService implements IGameService {
    private final GameContext context;
    private final ITurnStrategy turnStrategy;

    public GameService(GameContext context, ITurnStrategy turnStrategy) {
        this.context = context;
        this.turnStrategy = turnStrategy;
    }

    @Override
    public void addPlayer(Player player) {
        if (context.getGameState() != GameState.NEW) {
            throw new IllegalStateException("Cannot add player after the game has started");
        }
        context.addPlayer(player);
    }

    @Override
    public void startGame() {
        if (context.getPlayers().size() < 2) {
            throw new IllegalStateException("At least 2 players are required to start the game");
        }
        context.setGameState(GameState.IN_PROGRESS);
        context.setCurrentPlayer(turnStrategy.getFirstPlayer(context.getPlayers()));
    }

    @Override
    public void endGame() {
        context.setGameState(GameState.FINISHED);
    }

    @Override
    public int throwDice() {
        return context.throwDice();
    }

    @Override
    public Position movePlayer(Player player, int diceValue) {
        Board board = context.getBoard();
        int currentPos = player.getCurrentPosition().getValue();
        int newPos = currentPos + diceValue;
        try {
            Cell cell = board.getCell(newPos);
            Position finalPosition = cell.getDestination();
            player.setCurrentPosition(finalPosition);
            return finalPosition;
        } catch (IllegalArgumentException e) {
            // If new position is out of bounds, player stays in the same position
            return player.getCurrentPosition();
        }
    }

    @Override
    public Player getWinner() {
        return context.getWinner();
    }

    @Override
    public boolean playTurn() {
        if (context.isGameOver()) {
            throw new IllegalStateException("Game is already over");
        }
        Player currentPlayer = context.getCurrentPlayer();
        int diceValue = throwDice();
        Position newPosition = movePlayer(currentPlayer, diceValue);

        System.out.println(currentPlayer.getName() + " rolled a " + diceValue + " and moved to position "
                + newPosition.getValue());

        // check if current player has won
        if (currentPlayer.getCurrentPosition().getValue() == context.getBoard().getSize()) {
            context.setWinner(currentPlayer);
            endGame();
            System.out.println(currentPlayer.getName() + " wins the game!");
            return true;
        }

        // move to next player's turn
        Player nextPlayer = turnStrategy.getNextPlayer(context.getPlayers(), currentPlayer, diceValue);
        context.setCurrentPlayer(nextPlayer);
        return false;
    }

    @Override
    public boolean isGameOver() {
        return context.isGameOver();
    }
}
