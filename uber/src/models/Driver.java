package uber.src.models;

import java.util.concurrent.locks.ReentrantLock;

import uber.src.enums.DriverStatus;
import uber.src.models.base.User;

public class Driver extends User {
    // volatile ensures that changes to status are visible across threads,
    // which is important for concurrent access
    private volatile DriverStatus status;

    private final ReentrantLock lock;

    public Driver(String id, String name, String phoneNumber) {
        super(id, name, phoneNumber);
        this.status = DriverStatus.OFFLINE;
        this.lock = new ReentrantLock();
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == DriverStatus.AVAILABLE;
    }

    public boolean tryLockForRide() {
        if (!lock.tryLock()) {
            return false;
        }
        // we have the lock, check if driver is still available
        if (!isAvailable()) {
            lock.unlock(); // release lock if not available
            return false;
        }
        return true;
    }

    public void releaseLock() {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public void assignToRide() {
        this.status = DriverStatus.BUSY;
        this.releaseLock();
    }

    public void completeRide() {
        this.status = DriverStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Driver driver = (Driver) o;

        return getId().equals(driver.getId());
    }
}
