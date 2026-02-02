package services;

import java.time.LocalTime;
import java.util.Optional;

import models.PlatformAssignment;
import models.ScheduleRequest;
import models.ScheduleResponse;

public interface ISchedulingService {
    public ScheduleResponse scheduleTrain(ScheduleRequest request);

    public Optional<PlatformAssignment> getAssignmentForPlatformAtTime(int platformNumber, LocalTime time);
}
