package tictactoe.src.player;

import tictactoe.src.core.Board;
import tictactoe.src.core.Position;

public class CPUPlayerStrategy implements PlayerStrategy {
    public CPUPlayerStrategy() {
        // Constructor can be empty or used for initialization if needed
    }

    @Override
    public Position makeMove(Board board) {
        // Implement a simple strategy for the CPU player
        // For example, choose the first available position
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (board.isPositionEmpty(new Position(row, col))) {
                    return new Position(row, col);
                }
            }
        }
        return null; // No valid move available
    }

}
