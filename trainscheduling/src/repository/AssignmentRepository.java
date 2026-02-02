package repository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import models.PlatformAssignment;

public class AssignmentRepository implements IAssignmentRepository {
    Map<Integer, List<PlatformAssignment>> assignments;

    public AssignmentRepository() {
        this.assignments = new HashMap<>();
    }

    @Override
    public void save(PlatformAssignment assignment) {
        int platformNumber = assignment.getPlatform().getPlatformNumber();
        assignments.computeIfAbsent(platformNumber, k -> new ArrayList<>())
                .add(assignment);
    }

    @Override
    public Optional<PlatformAssignment> findByPlatformAndTime(int platformNumber, LocalTime time) {
        List<PlatformAssignment> platformAssignments = assignments.get(platformNumber);
        if (platformAssignments == null) {
            return Optional.empty();
        }

        for (PlatformAssignment assignment : platformAssignments) {
            if (assignment.getTimeWindow().getStartTime().compareTo(time) <= 0 &&
                    assignment.getTimeWindow().getEndTime().compareTo(time) >= 0) {
                return Optional.of(assignment);
            }
        }

        return Optional.empty();

    }

}
