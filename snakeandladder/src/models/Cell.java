package snakeandladder.src.models;

public abstract class Cell {
    protected Position source;
    protected Position destination;

    public Cell(Position source, Position destination) {
        this.source = source;
        this.destination = destination;
    }

    public Cell(Position source) {
        this.source = source;
        this.destination = source;
    }

    public abstract Position getDestination();
}
