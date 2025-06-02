package tictactoe.src.player;

import tictactoe.src.Board;
import tictactoe.src.Position;

public interface PlayerStrategy {
    public Position makeMove(Board board);
}
