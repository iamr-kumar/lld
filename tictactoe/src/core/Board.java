package tictactoe.src.core;

import tictactoe.src.player.Player;
import tictactoe.src.state.GameContext;

public class Board {
    private final int rows;
    private final int cols;
    private final Symbol[][] grid;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Symbol[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = Symbol.EMPTY;
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isValidMove(Position position) {
        if (position.getRow() < 0 || position.getRow() >= rows ||
                position.getCol() < 0 || position.getCol() >= cols) {
            return false;
        }
        return grid[position.getRow()][position.getCol()] == Symbol.EMPTY;
    }

    public void markMove(Position position, Symbol symbol) {
        grid[position.getRow()][position.getCol()] = symbol;
    }

    public Symbol getSymbolAt(Position position) {
        return grid[position.getRow()][position.getCol()];
    }

    public void checkGameState(GameContext context, Player currentPlayer) {
        for (int i = 0; i < rows; ++i) {
            if (grid[i][0] != Symbol.EMPTY &&
                    grid[i][0] == grid[i][1] && grid[i][1] == grid[i][2]) {
                context.next(currentPlayer, true, false);
                return;
            }
        }

        for (int j = 0; j < cols; ++j) {
            if (grid[0][j] != Symbol.EMPTY &&
                    grid[0][j] == grid[1][j] && grid[1][j] == grid[2][j]) {
                context.next(currentPlayer, true, false);
                return;
            }
        }

        if (grid[0][0] != Symbol.EMPTY &&
                grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {
            context.next(currentPlayer, true, false);
            return;
        }

        if (grid[0][2] != Symbol.EMPTY &&
                grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {
            context.next(currentPlayer, true, false);
            return;
        }

        if (isBoardFull()) {
            context.next(currentPlayer, false, true); // Game is a draw
        }

    }

    private boolean isBoardFull() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == Symbol.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public void printBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(grid[i][j].getSymbol() + " ");
            }
            System.out.println();
        }
    }

    public boolean isPositionEmpty(Position position) {
        return grid[position.getRow()][position.getCol()] == Symbol.EMPTY;
    }

}
