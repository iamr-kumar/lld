package snakeandladder.src;

import snakeandladder.src.context.GameContext;
import snakeandladder.src.models.Board;
import snakeandladder.src.models.LadderCell;
import snakeandladder.src.models.Player;
import snakeandladder.src.models.Position;
import snakeandladder.src.models.SnakeCell;
import snakeandladder.src.services.GameService;
import snakeandladder.src.strategy.ExtraTurnOnSixStrategy;
import snakeandladder.src.strategy.ITurnStrategy;

public class SnakeAndLadder {
    public static void main(String[] args) {
        System.out.println("Welcome to Snake and Ladder Game!");

        Board board = new Board(100);
        board.addCell(16, new SnakeCell(new Position(16), new Position(6)));
        board.addCell(47, new SnakeCell(new Position(47), new Position(26)));
        board.addCell(49, new SnakeCell(new Position(49), new Position(11)));
        board.addCell(56, new SnakeCell(new Position(56), new Position(53)));
        board.addCell(62, new SnakeCell(new Position(62), new Position(19)));
        board.addCell(64, new SnakeCell(new Position(64), new Position(60)));
        board.addCell(87, new SnakeCell(new Position(87), new Position(24)));
        board.addCell(93, new SnakeCell(new Position(93), new Position(73)));
        board.addCell(95, new SnakeCell(new Position(95), new Position(75)));
        board.addCell(98, new SnakeCell(new Position(98), new Position(78)));

        // Add ladders (bottom -> top)
        board.addCell(1, new LadderCell(new Position(1), new Position(38)));
        board.addCell(4, new LadderCell(new Position(4), new Position(14)));
        board.addCell(9, new LadderCell(new Position(9), new Position(31)));
        board.addCell(21, new LadderCell(new Position(21), new Position(42)));
        board.addCell(28, new LadderCell(new Position(28), new Position(84)));
        board.addCell(36, new LadderCell(new Position(36), new Position(44)));
        board.addCell(51, new LadderCell(new Position(51), new Position(67)));
        board.addCell(71, new LadderCell(new Position(71), new Position(91)));
        board.addCell(80, new LadderCell(new Position(80), new Position(100)));

        GameContext context = new GameContext(board);
        ITurnStrategy extraTurnOnSixStrategy = new ExtraTurnOnSixStrategy();
        GameService gameService = new GameService(context, extraTurnOnSixStrategy);

        gameService.addPlayer(new Player("Alice"));
        gameService.addPlayer(new Player("Bob"));

        gameService.startGame();
        while (!context.isGameOver()) {
            gameService.playTurn();
        }

        System.out.println("Game Over! Winner is: " + gameService.getWinner().getName());
    }
}
