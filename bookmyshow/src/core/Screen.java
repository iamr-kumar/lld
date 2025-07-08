package bookmyshow.src.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Screen {
    private final String id;
    private final List<Seat> seats;
    private final Integer screenNumber;
    private final Theatre theatre;

    public Screen(Integer screenNumber, Theatre theatre) {
        this.id = UUID.randomUUID().toString(); // Using hashCode of UUID for simplicity
        this.seats = new ArrayList<>();
        this.screenNumber = screenNumber;
        this.theatre = theatre;
    }

    public String getId() {
        return id;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public Integer getScreenNumber() {
        return screenNumber;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public boolean addSeat(Seat seat) {
        for (Seat existingSeat : seats) {
            if (existingSeat.getId().equals(seat.getId())) {
                return false;
            }
        }
        seats.add(seat);
        return true;
    }
}
