package bookmyshow.src.services;

import java.util.HashMap;
import java.util.Map;

import bookmyshow.src.core.Screen;
import bookmyshow.src.core.Seat;
import bookmyshow.src.core.Theatre;
import bookmyshow.src.types.SeatCategory;

public class TheatreService {
    private final Map<String, Theatre> theatres;
    private final Map<String, Screen> screens;
    private final Map<String, Seat> seats;

    public TheatreService() {
        this.theatres = new HashMap<>();
        this.screens = new HashMap<>();
        this.seats = new HashMap<>();
    }

    public Theatre createTheatre(String name) {
        Theatre theatre = new Theatre(name);
        theatres.put(theatre.getId(), theatre);
        return theatre;
    }

    public Screen createScreenInTheatre(Theatre theatre, Integer screenNumber) {
        if (!theatres.containsKey(theatre.getId())) {
            throw new IllegalArgumentException("Theatre does not exist");
        }
        Screen screen = new Screen(screenNumber, theatre);
        screens.put(screen.getId(), screen);
        return screen;
    }

    public Seat createSeatInScreen(Screen screen, String row, String seatId, SeatCategory seatCategory) {
        if (!screens.containsKey(screen.getId())) {
            throw new IllegalArgumentException("Screen does not exist");
        }
        Seat seat = new Seat(row, seatId, seatCategory);
        if (screen.addSeat(seat)) {
            seats.put(seat.getId(), seat);
            return seat;
        } else {
            throw new IllegalArgumentException("Seat already exists in this screen");
        }
    }

    public Theatre getTheatreById(String id) {
        return theatres.get(id);
    }

    public Screen getScreenById(String id) {
        return screens.get(id);
    }

    public Seat getSeatById(String id) {
        return seats.get(id);
    }
}
