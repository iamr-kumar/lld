package player;

import core.Board;
import core.Position;

public interface PlayerStrategy {
    public Position makeMove(Board board);
}
