package bookmyshow.interfaces;

import java.util.List;

import bookmyshow.core.Seat;
import bookmyshow.core.Show;
import bookmyshow.core.User;

public interface ISeatLockProvider {
    void lockSeat(Show show, List<Seat> seats, User user) throws Exception;

    void unlockSeats(Show show, List<Seat> seats, User user) throws Exception;

    boolean validateLock(Seat seat, Show show, User user);

    List<Seat> getLockedSeats(Show show) throws Exception;
}
