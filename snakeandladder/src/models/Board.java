package snakeandladder.src.models;

public class Board {
    private final int size;
    private final Cell[] cells;

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size + 1];
        // initalize all cells as regular cells
        for (int i = 0; i <= size; i++) {
            this.cells[i] = new RegularCell(new Position(i));
        }
    }

    public void addCell(int position, Cell cell) {
        validatePosition(position);
        if (cell == null) {
            throw new IllegalArgumentException("Cell cannot be null");
        }
        this.cells[position] = cell;
    }

    public Cell getCell(int position) {
        validatePosition(position);
        return this.cells[position];
    }

    public int getSize() {
        return this.size;
    }

    private void validatePosition(int position) {
        if (position < 0 || position > this.size) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    public Position getEndOfBoard() {
        return new Position(this.size);
    }
}
