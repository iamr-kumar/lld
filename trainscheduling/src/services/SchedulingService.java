package services;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import enums.Status;
import manager.IPlatformAvailabilityManager;
import models.PlatformAssignment;
import models.PlatformState;
import models.ScheduleRequest;
import models.ScheduleResponse;
import models.TimeWindow;
import models.Train;
import repository.IAssignmentRepository;

public class SchedulingService implements ISchedulingService {
    private final IPlatformAvailabilityManager platformAvailabilityManager;
    private final IAssignmentRepository assignmentRepository;
    private final ReadWriteLock rwLock;

    public SchedulingService(IPlatformAvailabilityManager platformAvailabilityManager,
            IAssignmentRepository assignmentRepository, ReadWriteLock rwLock) {
        this.platformAvailabilityManager = platformAvailabilityManager;
        this.assignmentRepository = assignmentRepository;
        this.rwLock = new ReentrantReadWriteLock();

    }

    @Override
    public ScheduleResponse scheduleTrain(ScheduleRequest request) {
        LocalTime trainArrivalTime = request.getTimeWindow().getStartTime();
        LocalTime trainDepartureTime = request.getTimeWindow().getEndTime();
        Train train = request.getTrain();
        rwLock.writeLock().lock();
        try {
            PlatformState earliestPlatform = platformAvailabilityManager.getNextAvailablePlatform();
            LocalTime updatedNextAvailableTime = trainDepartureTime;
            Status scheduleStatus;
            TimeWindow assignmentWindow = request.getTimeWindow();
            if (earliestPlatform.getNextAvailableTime().compareTo(trainArrivalTime) <= 0) {
                updatedNextAvailableTime = trainDepartureTime;
                scheduleStatus = Status.SCHEDULED;

            } else {
                scheduleStatus = Status.WAITING;
                LocalTime newArrivalTime = earliestPlatform.getNextAvailableTime();
                LocalTime newDepartureTime = getNewDeparturTime(newArrivalTime, trainArrivalTime, trainDepartureTime);
                assignmentWindow = new TimeWindow(newArrivalTime, newDepartureTime);
                updatedNextAvailableTime = newDepartureTime;
            }
            PlatformAssignment newAssignment = new PlatformAssignment(train, earliestPlatform.getPlatform(),

                    assignmentWindow);
            assignmentRepository.save(newAssignment);
            PlatformState updatedPlatformState = new PlatformState(earliestPlatform.getPlatform(),
                    updatedNextAvailableTime);
            platformAvailabilityManager.updatePlatformAvailability(updatedPlatformState);
            return new ScheduleResponse(scheduleStatus, newAssignment);
        } finally {
            rwLock.writeLock().unlock();
        }

    }

    @Override
    public Optional<PlatformAssignment> getAssignmentForPlatformAtTime(int platformNumber, LocalTime time) {
        rwLock.readLock().lock();
        try {
            return assignmentRepository.findByPlatformAndTime(platformNumber, time);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    private LocalTime getNewDeparturTime(LocalTime newArrivalTime, LocalTime originalArrivalTime,
            LocalTime originalDepartureTime) {
        // Simple logic: maintain the same duration as the original schedule
        // Handle when times cross midnight if needed
        int originalDurationMinutes = (int) Duration.between(originalArrivalTime, originalDepartureTime).toMinutes();
        return newArrivalTime.plusMinutes(originalDurationMinutes);
    }

}
