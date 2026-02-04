package meetingscheduler.src.models;

import java.util.List;
import java.util.UUID;

import meetingscheduler.src.enums.MeetingStatus;

public class Meeting {
    private final String title;
    private final UUID id;
    private TimeWindow window;
    private Room room;
    private MeetingStatus status;
    private final User host;
    private List<User> attendees;

    public Meeting(String title, TimeWindow window, User host, List<User> attendees) {
        this.title = title;
        this.id = UUID.randomUUID();
        this.window = window;
        this.status = MeetingStatus.SCHEDULED;
        this.attendees = attendees;
        this.host = host;
    }

    public String getTitle() {
        return this.title;
    }

    public UUID getId() {
        return this.id;
    }

    public TimeWindow getWindow() {
        return this.window;
    }

    public void assignRoom(Room room) {
        this.room = room;
    }

    public void updateStatus(MeetingStatus status) {
        this.status = status;
    }

    public Room getRoom() {
        return this.room;
    }

    public MeetingStatus getStatus() {
        return this.status;
    }

    public User getHost() {
        return this.host;
    }

    public List<User> getAttendees() {
        return this.attendees;
    }

    public void addAttendee(User user) {
        this.attendees.add(user);
    }
}
