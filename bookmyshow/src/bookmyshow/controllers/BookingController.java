package bookmyshow.controllers;

import java.util.ArrayList;
import java.util.List;

import bookmyshow.core.Booking;
import bookmyshow.core.Seat;
import bookmyshow.core.Show;
import bookmyshow.core.User;
import bookmyshow.services.BookingService;
import bookmyshow.services.ShowService;
import bookmyshow.services.TheatreService;

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
