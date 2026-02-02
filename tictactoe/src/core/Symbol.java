package core;

public enum Symbol {
    X, O, EMPTY;

    public String getSymbol() {
        return this.name();
    }
}
