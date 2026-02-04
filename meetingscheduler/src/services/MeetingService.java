package meetingscheduler.src.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import meetingscheduler.src.enums.RoomType;
import meetingscheduler.src.models.Meeting;
import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.TimeWindow;
import meetingscheduler.src.models.User;
import meetingscheduler.src.repository.IMeetingRepository;

public class MeetingService implements IMeetingService {
    private final IRoomService roomService;
    private final IMeetingRepository meetingRepository;
    private final Map<Room, ReentrantReadWriteLock> roomLocks;

    public MeetingService(IRoomService roomService, IMeetingRepository meetingRepository) {
        this.roomService = roomService;
        this.meetingRepository = meetingRepository;
        this.roomLocks = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Meeting> scheduleMeeting(User user, String title, TimeWindow window, List<User> attendees,
            RoomType roomType) {
        if (user == null || !isValidTimeWindow(window) || attendees == null || attendees.isEmpty()) {
            return Optional.empty();
        }

        Optional<List<Room>> availableRoom = this.roomService.getAvailableRoomForRequest(window, attendees.size(),
                roomType);
        if (availableRoom.isEmpty()) {
            return Optional.empty();
        }

        for (Room room : availableRoom.get()) {
            ReentrantReadWriteLock lock = roomLocks.computeIfAbsent(room, r -> new ReentrantReadWriteLock());
            try {
                lock.writeLock().lock();
                if (!roomService.isRoomAvailable(room, window)) {
                    continue;
                }

                Meeting meeting = new Meeting(title, window, user, attendees);
                meeting.assignRoom(room);
                roomService.addBookingToRoom(room, window);
                this.meetingRepository.save(meeting);
                return Optional.of(meeting);
            } finally {
                lock.writeLock().unlock();
            }
        }

        return Optional.empty();

    }

    @Override
    public boolean cancelMeeting(Meeting meeting) {
        if (meeting == null || meeting.getRoom() == null) {
            return false;
        }

        Room room = meeting.getRoom();
        ReentrantReadWriteLock lock = roomLocks.computeIfAbsent(room, r -> new ReentrantReadWriteLock());
        try {
            lock.writeLock().lock();
            boolean removedFromRepo = this.meetingRepository.remove(meeting);
            if (!removedFromRepo) {
                return false;
            }
            boolean removedFromRoom = this.roomService.removeBookingFromRoom(room, meeting.getWindow());
            return removedFromRoom;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<Meeting> getAllMeetingsForUser(User user) {
        return this.meetingRepository.getAllMeetingsForUser(user);
    }

    private boolean isValidTimeWindow(TimeWindow window) {
        return window != null && window.getStartTime().isBefore(window.getEndTime());
    }
}
