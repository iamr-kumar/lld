package meetingscheduler.src.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import meetingscheduler.src.models.Meeting;
import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.User;

public class MeetingRepository implements IMeetingRepository {
    private final TreeMap<LocalDateTime, TreeSet<Meeting>> meetings;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    public MeetingRepository() {
        this.meetings = new TreeMap<>();
    }

    @Override
    public boolean save(Meeting meeting) {
        LocalDateTime startTime = meeting.getWindow().getStartTime();
        try {
            rwLock.writeLock().lock();
            meetings.putIfAbsent(startTime,
                    new TreeSet<>((a, b) -> a.getWindow().getEndTime().compareTo(b.getWindow().getEndTime())));
            TreeSet<Meeting> meetingSet = meetings.get(startTime);
            return meetingSet.add(meeting);
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    @Override
    public boolean remove(Meeting meeting) {
        LocalDateTime startTime = meeting.getWindow().getStartTime();
        try {
            rwLock.writeLock().lock();
            TreeSet<Meeting> meetingSet = meetings.getOrDefault(startTime, null);
            if (meetingSet == null || meetingSet.isEmpty()) {
                return false;
            }
            boolean removed = meetingSet.remove(meeting);
            if (meetingSet.isEmpty()) {
                meetings.remove(startTime);
            }
            return removed;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public List<Meeting> getAllMeetingsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<Meeting> result = new ArrayList<>();
        try {
            rwLock.readLock().lock();
            for (Map.Entry<LocalDateTime, TreeSet<Meeting>> entry : meetings.subMap(startTime, endTime).entrySet()) {
                result.addAll(entry.getValue());
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return result;
    }

    @Override
    public List<Meeting> getAllMeetingsForRoom(Room room) {
        List<Meeting> result = new ArrayList<>();
        try {
            rwLock.readLock().lock();
            for (Map.Entry<LocalDateTime, TreeSet<Meeting>> entry : meetings.entrySet()) {
                for (Meeting meeting : entry.getValue()) {
                    if (meeting.getRoom().equals(room)) {
                        result.add(meeting);
                    }
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return result;
    }

    @Override
    public List<Meeting> getAllMeetingsForUser(User user) {
        List<Meeting> result = new ArrayList<>();
        try {
            rwLock.readLock().lock();
            for (Map.Entry<LocalDateTime, TreeSet<Meeting>> entry : meetings.entrySet()) {
                for (Meeting meeting : entry.getValue()) {
                    if (meeting.getHost().equals(user) || meeting.getAttendees().contains(user)) {
                        result.add(meeting);
                    }
                }
            }
        } finally {
            rwLock.readLock().unlock();
        }
        return result;
    }
}