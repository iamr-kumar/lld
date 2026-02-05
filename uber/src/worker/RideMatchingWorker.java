package uber.src.worker;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import uber.src.models.Driver;
import uber.src.models.RideRequest;
import uber.src.services.IRideMatchingService;

public class RideMatchingWorker implements Runnable {
    private final BlockingQueue<RideRequest> rideRequestQueue;
    private final IRideMatchingService rideMatchingService;
    private volatile boolean isRunning;

    public RideMatchingWorker(BlockingQueue<RideRequest> rideRequestQueue, IRideMatchingService rideMatchingService) {
        this.rideRequestQueue = rideRequestQueue;
        this.rideMatchingService = rideMatchingService;
        this.isRunning = true;
    }

    @Override
    public void run() {
        System.out.println("RideMatchingWorker started.");
        while (this.isRunning) {
            try {
                RideRequest rideRequest = rideRequestQueue.poll(1, TimeUnit.SECONDS);
                if (rideRequest == null) {
                    continue;
                }

                System.out.println("Worker picked up a ride request: " + rideRequest.getRide().getId());

                Optional<Driver> driverOpt = rideMatchingService.findAndAssignDriver(rideRequest.getRide());
                if (driverOpt.isPresent()) {
                    rideRequest.complete(driverOpt.get());
                } else {
                    rideRequest.fail();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("RideMatchingWorker interrupted.");
            }
        }
        System.out.println("RideMatchingWorker stopped.");
    }

    public void stop() {
        this.isRunning = false;
    }
}
