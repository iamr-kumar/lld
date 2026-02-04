package meetingscheduler.src.repository;

import java.time.LocalDateTime;
import java.util.List;

import meetingscheduler.src.models.Meeting;
import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.User;

public interface IMeetingRepository {
    public boolean save(Meeting meeting);

    public boolean remove(Meeting meeting);

    public List<Meeting> getAllMeetingsInTimeRange(LocalDateTime startTime,
            LocalDateTime endTime);

    public List<Meeting> getAllMeetingsForRoom(Room room);

    public List<Meeting> getAllMeetingsForUser(User user);
}
