package snakeandladder.src.models;

public class Player {
    private String name;
    private Position currentPosition;

    public Player(String name) {
        this.name = name;
        this.currentPosition = new Position(0);
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    public String getName() {
        return name;
    }
}
