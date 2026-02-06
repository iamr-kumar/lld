package snakeandladder.src.models;

public class RegularCell extends Cell {
    public RegularCell(Position source) {
        super(source);
    }

    @Override
    public Position getDestination() {
        return this.destination;
    }
}
