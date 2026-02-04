package bookmyshow.src.providers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bookmyshow.src.core.Seat;
import bookmyshow.src.core.SeatLock;
import bookmyshow.src.core.Show;
import bookmyshow.src.core.User;
import bookmyshow.src.interfaces.ISeatLockProvider;

public class SeatLockProvider implements ISeatLockProvider {
    private final Integer lockTimeout;

    private final Map<Show, Map<Seat, SeatLock>> seatLocks;

    public SeatLockProvider(Integer lockTimeout) {
        this.lockTimeout = lockTimeout;
        this.seatLocks = new HashMap<>();
    }

    @Override
    public void lockSeat(Show show, List<Seat> seats, User user) throws Exception {
        if (show == null || seats == null || user == null) {
            throw new IllegalArgumentException("Show, seats, and user cannot be null");
        }
        Map<Seat, SeatLock> locks = seatLocks.computeIfAbsent(show, s -> new ConcurrentHashMap<>());
        synchronized (locks) {
            for (Seat seat : seats) {
                if (locks.containsKey(seat)) {
                    SeatLock currentLock = locks.get(seat);
                    if (!currentLock.isLockExpired()) {
                        throw new Exception("Seat " + seat.getId() + " is already locked by another user.");
                    } else {
                        locks.remove(seat);
                    }
                }
            }

            for (Seat seat : seats) {
                SeatLock seatLock = new SeatLock(seat, show, lockTimeout, user);
                locks.put(seat, seatLock);
            }
        }
    }

    @Override
    public void unlockSeats(Show show, List<Seat> seats, User user) throws Exception {
        if (show == null || seats == null || user == null) {
            throw new IllegalArgumentException("Show, seats, and user cannot be null");
        }
        Map<Seat, SeatLock> locks = seatLocks.get(show);
        if (locks == null) {
            return;
        }
        synchronized (locks) {
            for (Seat seat : seats) {
                SeatLock currentLock = locks.get(seat);
                if (currentLock != null && currentLock.getUser().equals(user)) {
                    locks.remove(seat);
                } else {
                    throw new Exception("Seat " + seat.getId() + " is not locked by the user.");
                }
            }
        }
    }

    @Override
    public boolean validateLock(Seat seat, Show show, User user) {
        Map<Seat, SeatLock> locks = seatLocks.get(show);
        if (locks == null) {
            return false;
        }
        synchronized (locks) {
            SeatLock seatLock = locks.get(seat);
            return seatLock != null && !seatLock.isLockExpired() && seatLock.getUser().equals(user);
        }

    }

    @Override
    public List<Seat> getLockedSeats(Show show) {
        Map<Seat, SeatLock> locks = seatLocks.get(show);
        if (locks == null) {
            return List.of();
        }
        synchronized (locks) {
            return locks.values().stream()
                    .filter(lock -> !lock.isLockExpired())
                    .map(SeatLock::getSeat)
                    .toList();
        }
    }

}
