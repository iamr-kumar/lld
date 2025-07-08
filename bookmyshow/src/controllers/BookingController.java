package bookmyshow.src.controllers;

import java.util.ArrayList;
import java.util.List;

import bookmyshow.src.core.Booking;
import bookmyshow.src.core.Seat;
import bookmyshow.src.core.Show;
import bookmyshow.src.core.User;
import bookmyshow.src.services.BookingService;
import bookmyshow.src.services.ShowService;
import bookmyshow.src.services.TheatreService;

public class BookingController {
    private final TheatreService theatreService;
    private final BookingService bookingService;
    private final ShowService showService;

    public BookingController(TheatreService theatreService, BookingService bookingService, ShowService showService) {
        this.theatreService = theatreService;
        this.bookingService = bookingService;
        this.showService = showService;
    }

    public String createBooking(String showId, User user, List<String> seatIds) throws Exception {
        Show show = showService.getShowById(showId);
        if (show == null) {
            throw new IllegalArgumentException("Show not found");
        }
        List<Seat> seats = new ArrayList<>();
        for (String seatId : seatIds) {
            Seat seat = theatreService.getSeatById(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Seat not found: " + seatId);
            }
            seats.add(seat);
        }
        Booking booking = bookingService.createBooking(show, user, seats);
        return booking.getId();
    }
}
