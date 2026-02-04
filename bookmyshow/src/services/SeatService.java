package bookmyshow.src.services;

import java.util.List;

import bookmyshow.src.core.Seat;
import bookmyshow.src.core.Show;
import bookmyshow.src.interfaces.ISeatLockProvider;

public class SeatService {
    private final BookingService bookingService;
    private final ISeatLockProvider seatLockProvider;

    public SeatService(BookingService bookingService, ISeatLockProvider seatLockProvider) {
        this.bookingService = bookingService;
        this.seatLockProvider = seatLockProvider;
    }

    public List<Seat> getAvailableSeats(Show show) throws Exception {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }
        List<Seat> allSeatsForShow = show.getScreen().getSeats();
        List<Seat> unavailableSeats = this.getUnavailableSeats(show);
        allSeatsForShow.removeAll(unavailableSeats);
        return allSeatsForShow;
    }

    private List<Seat> getUnavailableSeats(Show show) throws Exception {
        if (show == null) {
            throw new IllegalArgumentException("Show cannot be null");
        }

        List<Seat> unavailableSeats = bookingService.getBookedSeats(show);
        List<Seat> lockedSeats = seatLockProvider.getLockedSeats(show);
        unavailableSeats.addAll(lockedSeats);
        return unavailableSeats;
    }
}
