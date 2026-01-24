package bookmyshow.controllers;

import bookmyshow.core.Screen;
import bookmyshow.core.Theatre;
import bookmyshow.services.TheatreService;
import bookmyshow.types.SeatCategory;

public class TheatreController {
    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    public String addTheatre(String name) {
        Theatre theatre = theatreService.createTheatre(name);
        return theatre.getId();
    }

    public Theatre getTheatre(String id) {
        return theatreService.getTheatreById(id);
    }

    public String createScreenInTheatre(String theatreId, Integer screenNumber) {
        Theatre theatre = theatreService.getTheatreById(theatreId);
        if (theatre == null) {
            throw new IllegalArgumentException("Theatre not found");
        }
        Screen screen = theatreService.createScreenInTheatre(theatre, screenNumber);
        return screen.getId();
    }

    public String createSeatInScreen(String screenId, String row, String seatNumber, SeatCategory category) {
        Screen screen = theatreService.getScreenById(screenId);
        if (screen == null) {
            throw new IllegalArgumentException("Screen not found");
        }
        return theatreService.createSeatInScreen(screen, row, seatNumber, category).getId();
    }
}
