package meetingscheduler.src.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import meetingscheduler.src.enums.RoomType;
import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.TimeWindow;
import meetingscheduler.src.repository.IRoomRepository;

public class RoomService implements IRoomService {
    private final IRoomRepository roomRepository;

    public RoomService(IRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Optional<List<Room>> getAvailableRoomForRequest(TimeWindow window, int numberOfAttendees,
            RoomType roomType) {
        List<Room> options = new ArrayList<>();

        Optional<List<Room>> freeRoomOptions = this.roomRepository.getFreeRoomsForTimeWindow(window);
        if (freeRoomOptions.isEmpty()) {
            return Optional.empty();
        }
        for (Room room : freeRoomOptions.get()) {
            if (room.getCapacity() >= numberOfAttendees && room.getRoomType() == roomType) {
                options.add(room);
            }
        }
        return options.isEmpty() ? Optional.empty() : Optional.of(options);
    }

    @Override
    public boolean isRoomAvailable(Room room, TimeWindow window) {
        return this.roomRepository.isRoomAvailable(room, window);
    }

    @Override
    public boolean addBookingToRoom(Room room, TimeWindow window) {

        return this.roomRepository.addBookingToRoom(room, window);

    }

    @Override
    public boolean removeBookingFromRoom(Room room, TimeWindow window) {
        return this.roomRepository.removeBookingFromRoom(room, window);

    }

    @Override
    public boolean addRoom(Room room) {

        this.roomRepository.addRoom(room);
        return true;

    }
}
