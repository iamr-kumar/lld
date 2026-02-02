package repository;

import java.time.LocalTime;
import java.util.Optional;

import models.PlatformAssignment;

public interface IAssignmentRepository {
    void save(PlatformAssignment assignment);

    Optional<PlatformAssignment> findByPlatformAndTime(int platformNumber, LocalTime time);
}
