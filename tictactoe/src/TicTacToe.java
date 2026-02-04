package tictactoe.src;

import tictactoe.src.core.Board;
import tictactoe.src.core.Position;
import tictactoe.src.core.Symbol;
import tictactoe.src.player.CPUPlayerStrategy;
import tictactoe.src.player.HumanPlayerStrategy;
import tictactoe.src.player.Player;
import tictactoe.src.state.GameContext;
import tictactoe.src.state.GameOutcome;
import tictactoe.src.state.GameState;

public class TicTacToe {
    private Board board;
    private Player playerX;
    private Player playerO;
    private GameContext gameContext;
    private Player currentPlayer;

    public TicTacToe() {
        this.board = new Board(3, 3);
        this.playerX = new Player(Symbol.X, new HumanPlayerStrategy("Player X"));
        this.playerO = new Player(Symbol.O, new CPUPlayerStrategy());
        this.gameContext = new GameContext();
        this.currentPlayer = playerX; // X starts first
    }

    public void play() {
        do {
            board.printBoard();
            Position move = currentPlayer.makeMove(board);
            board.markMove(move, currentPlayer.getSymbol());
            board.checkGameState(gameContext, currentPlayer);
            switchPlayer();
        } while (!gameContext.isGameOver());

        announceResult();
    }

    private void switchPlayer() {
        if (currentPlayer == playerX) {
            currentPlayer = playerO;
        } else {
            currentPlayer = playerX;
        }
    }

    private void announceResult() {
        GameState state = gameContext.getGameState();
        GameOutcome outcome = state.getGameOutcome();
        switch (outcome) {
            case X_WIN:
                System.out.println("Player X wins!");
                break;
            case O_WIN:
                System.out.println("Player O wins!");
                break;
            case DRAW:
                System.out.println("The game is a draw!");
                break;
            default:
                System.out.println("The game is still ongoing.");
                break;
        }
    }
}
