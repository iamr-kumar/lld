package meetingscheduler.src.services;

import java.util.List;
import java.util.Optional;

import meetingscheduler.src.enums.RoomType;
import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.TimeWindow;

public interface IRoomService {
    public boolean addRoom(Room room);

    public boolean addBookingToRoom(Room room, TimeWindow window);

    public boolean removeBookingFromRoom(Room room, TimeWindow window);

    public Optional<List<Room>> getAvailableRoomForRequest(TimeWindow window, int numberOfAttendees, RoomType roomType);

    public boolean isRoomAvailable(Room room, TimeWindow window);
}
