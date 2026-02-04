package meetingscheduler.src.services;

import java.util.List;
import java.util.Optional;

import meetingscheduler.src.enums.RoomType;
import meetingscheduler.src.models.Meeting;
import meetingscheduler.src.models.TimeWindow;
import meetingscheduler.src.models.User;

public interface IMeetingService {
    public Optional<Meeting> scheduleMeeting(User user, String title, TimeWindow window, List<User> attendees,
            RoomType roomType);

    public boolean cancelMeeting(Meeting meeting);

    public List<Meeting> getAllMeetingsForUser(User user);
}
