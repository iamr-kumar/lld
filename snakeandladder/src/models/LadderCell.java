package snakeandladder.src.models;

public class LadderCell extends Cell {
    public LadderCell(Position source, Position destination) {
        super(source, destination);
        int comp = source.compareTo(destination);
        if (comp >= 0) {
            throw new IllegalArgumentException(
                    "Source position must be less than destination position for a ladder cell");
        }
    }

    @Override
    public Position getDestination() {
        return this.destination;
    }
}
