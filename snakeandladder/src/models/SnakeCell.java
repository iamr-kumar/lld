package snakeandladder.src.models;

public class SnakeCell extends Cell {
    public SnakeCell(Position source, Position destination) {
        super(source, destination);
        int comp = source.compareTo(destination);
        if (comp <= 0) {
            throw new IllegalArgumentException(
                    "Source position must be greater than destination position for a snake cell");
        }
    }

    @Override
    public Position getDestination() {
        return this.destination;
    }
}
