package meetingscheduler.src.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.RoomBookings;
import meetingscheduler.src.models.TimeWindow;

public class RoomRepository implements IRoomRepository {
    private final Map<Room, RoomBookings> rooms;

    public RoomRepository() {
        rooms = new ConcurrentHashMap<>();
    }

    @Override
    public void addRoom(Room room) {
        rooms.putIfAbsent(room, new RoomBookings(room));
    }

    @Override
    public boolean addBookingToRoom(Room room, TimeWindow window) {

        RoomBookings bookings = rooms.getOrDefault(room, null);
        if (bookings == null)
            return false;
        bookings.addBooking(window);
        return true;

    }

    @Override
    public boolean removeBookingFromRoom(Room room, TimeWindow window) {

        RoomBookings bookings = rooms.getOrDefault(room, null);
        if (bookings == null) {
            return false;
        }
        return bookings.removeBooking(window);

    }

    @Override
    public Optional<List<Room>> getFreeRoomsForTimeWindow(TimeWindow window) {
        List<Room> result = new ArrayList<>();

        for (Map.Entry<Room, RoomBookings> entry : rooms.entrySet()) {
            Room room = entry.getKey();
            RoomBookings bookings = entry.getValue();

            if (bookings.isTimeWindowAvailable(window)) {
                result.add(room);
            }
        }
        return Optional.of(result);

    }

    @Override
    public boolean isRoomAvailable(Room room, TimeWindow window) {
        RoomBookings bookings = rooms.getOrDefault(room, null);
        if (bookings == null) {
            return true;
        }
        return bookings.isTimeWindowAvailable(window);
    }
}