package bookmyshow.core;

import java.util.UUID;

import bookmyshow.types.SeatCategory;

public class Seat {
    private final String id;
    private final String row;
    private final String number;
    private final SeatCategory category;

    public Seat(String row, String number, SeatCategory category) {
        this.id = UUID.randomUUID().toString();
        this.row = row;
        this.number = number;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public SeatCategory getCategory() {
        return category;
    }

    public String getRow() {
        return row;
    }

    public String getNumber() {
        return number;
    }
}
