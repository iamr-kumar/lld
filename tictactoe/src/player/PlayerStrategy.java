package tictactoe.src.player;

import tictactoe.src.core.Board;
import tictactoe.src.core.Position;

public interface PlayerStrategy {
    public Position makeMove(Board board);
}
