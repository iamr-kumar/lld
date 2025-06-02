package tictactoe.src.player;

import tictactoe.src.Board;
import tictactoe.src.Position;
import tictactoe.src.Symbol;

public class Player {
    private final Symbol symbol;
    private final PlayerStrategy strategy;

    public Player(Symbol symbol, PlayerStrategy strategy) {
        this.symbol = symbol;
        this.strategy = strategy;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Position makeMove(Board board) {
        return strategy.makeMove(board);
    }
}
