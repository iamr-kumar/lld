package tictactoe.src.player;

import tictactoe.src.core.Board;
import tictactoe.src.core.Position;
import tictactoe.src.core.Symbol;

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
