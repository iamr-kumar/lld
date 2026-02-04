package meetingscheduler.src.repository;

import java.util.List;
import java.util.Optional;

import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.TimeWindow;

public interface IRoomRepository {
    public void addRoom(Room room);

    public boolean addBookingToRoom(Room room, TimeWindow window);

    public boolean removeBookingFromRoom(Room room, TimeWindow window);

    public Optional<List<Room>> getFreeRoomsForTimeWindow(TimeWindow window);

    public boolean isRoomAvailable(Room room, TimeWindow window);
}
