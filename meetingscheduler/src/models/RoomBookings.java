package meetingscheduler.src.models;

import java.util.TreeSet;

public class RoomBookings {
    private final Room room;
    private final TreeSet<TimeWindow> bookings;

    public RoomBookings(Room room) {
        this.room = room;
        bookings = new TreeSet<TimeWindow>(
                (a, b) -> a.getStartTime().compareTo(b.getStartTime()));
    }

    public Room getRoom() {
        return this.room;
    }

    // Requests for the same room will always be syncronized by service level lock
    // safe to use synchronized here
    public synchronized TreeSet<TimeWindow> getBookingsForRoom() {
        return this.bookings;
    }

    public synchronized boolean addBooking(TimeWindow window) {
        return this.bookings.add(window);
    }

    public synchronized boolean removeBooking(TimeWindow window) {
        return this.bookings.remove(window);
    }

    public synchronized boolean isTimeWindowAvailable(TimeWindow window) {
        TimeWindow floor = bookings.floor(window);
        if (floor != null && floor.overlapsWith(window)) {
            return false;
        }
        TimeWindow ceiling = bookings.ceiling(window);
        if (ceiling != null && ceiling.overlapsWith(window)) {
            return false;
        }
        return true;
    }

}
