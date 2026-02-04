package trainscheduling.src.services;

import java.time.LocalTime;
import java.util.Optional;

import trainscheduling.src.models.PlatformAssignment;
import trainscheduling.src.models.ScheduleRequest;
import trainscheduling.src.models.ScheduleResponse;

public interface ISchedulingService {
    public ScheduleResponse scheduleTrain(ScheduleRequest request);

    public Optional<PlatformAssignment> getAssignmentForPlatformAtTime(int platformNumber, LocalTime time);
}
