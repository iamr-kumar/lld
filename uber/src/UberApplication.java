package uber.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import uber.src.enums.DriverStatus;
import uber.src.models.Driver;
import uber.src.models.Location;
import uber.src.models.Ride;
import uber.src.models.RideRequest;
import uber.src.models.Rider;
import uber.src.repositories.DriverRepository;
import uber.src.repositories.IDriverRepository;
import uber.src.repositories.IRideRepository;
import uber.src.repositories.RideRepository;
import uber.src.services.DriverNotificationService;
import uber.src.services.IDriverNotificationService;
import uber.src.services.IRideMatchingService;
import uber.src.services.IRideService;
import uber.src.services.RideMatchingService;
import uber.src.services.RideService;
import uber.src.strategies.IDriverMatchingStrategy;
import uber.src.strategies.NearestDriverStrategy;
import uber.src.worker.RideMatchingWorker;

public class UberApplication {

    private final IDriverRepository driverRepository;
    private final IRideRepository rideRepository;
    private final BlockingQueue<RideRequest> requestQueue;
    private final IDriverNotificationService driverNotificationService;
    private final IRideMatchingService rideMatchingService;
    private final IRideService rideService;
    private final ExecutorService workerPool;
    private final List<RideMatchingWorker> workers;

    private static final int NUM_WORKERS = 5;
    private static final int NUM_DRIVERS = 10;

    public UberApplication() {
        this.driverRepository = new DriverRepository();
        this.rideRepository = new RideRepository();
        this.requestQueue = new LinkedBlockingQueue<>(100);

        this.driverNotificationService = new DriverNotificationService();
        IDriverMatchingStrategy driverMatchingStrategy = new NearestDriverStrategy();
        this.rideMatchingService = new RideMatchingService(driverRepository, driverMatchingStrategy,
                driverNotificationService);
        this.rideService = new RideService(rideRepository, requestQueue);

        this.workerPool = Executors.newFixedThreadPool(NUM_WORKERS);
        this.workers = new ArrayList<>();
    }

    public void initialize() {
        System.out.println("=".repeat(60));
        System.out.println("Welcome to the Uber Application!");
        System.out.println("=".repeat(60));

        // Start worker threads for ride matching
        for (int i = 0; i < NUM_WORKERS; i++) {
            RideMatchingWorker worker = new RideMatchingWorker(requestQueue, rideMatchingService);
            workers.add(worker);
            workerPool.submit(worker);
        }
        System.out.println("Started " + NUM_WORKERS + " ride matching workers.");

        // Add drivers and set them as AVAILABLE
        for (int i = 1; i <= NUM_DRIVERS; i++) {
            Driver driver = new Driver("driver" + i, "Driver " + i, "555-000" + i);
            driver.setCurrentLocation(new Location(i * 2.0, i * 2.0));
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.addDriver(driver);
        }
        System.out.println("Added " + NUM_DRIVERS + " available drivers.");
        System.out.println();
    }

    /**
     * Demonstrates synchronous ride request - blocks until driver is assigned or
     * timeout
     */
    public void demonstrateSyncRide() {
        System.out.println("=".repeat(60));
        System.out.println("DEMO: Synchronous Ride Request");
        System.out.println("=".repeat(60));

        Rider rider = new Rider("rider1", "John Doe", "555-1111");
        Location pickup = new Location(3.0, 3.0);
        Location dropoff = new Location(10.0, 10.0);

        System.out.println("Rider " + rider.getName() + " requesting ride from " + pickup + " to " + dropoff);

        long startTime = System.currentTimeMillis();
        Optional<Ride> rideOpt = rideService.requestRide(rider, pickup, dropoff);
        long duration = System.currentTimeMillis() - startTime;

        if (rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            System.out.println("SUCCESS: Ride " + ride.getId() + " assigned to driver "
                    + ride.getDriver().getName() + " (took " + duration + "ms)");

            // Complete the ride
            rideService.completeRide(ride.getId());
            System.out.println("Ride completed!");
        } else {
            System.out.println("FAILED: No driver available for ride (took " + duration + "ms)");
        }
        System.out.println();
    }

    /**
     * Demonstrates asynchronous ride request - returns immediately with a future
     */
    public void demonstrateAsyncRide() {
        System.out.println("=".repeat(60));
        System.out.println("DEMO: Asynchronous Ride Request");
        System.out.println("=".repeat(60));

        Rider rider = new Rider("rider2", "Jane Smith", "555-2222");
        Location pickup = new Location(5.0, 5.0);
        Location dropoff = new Location(15.0, 15.0);

        System.out.println("Rider " + rider.getName() + " requesting ride asynchronously...");

        long startTime = System.currentTimeMillis();
        RideRequest rideRequest = rideService.requestRideAsync(rider, pickup, dropoff);
        long submitDuration = System.currentTimeMillis() - startTime;
        System.out.println("Request submitted in " + submitDuration + "ms (non-blocking)");

        // Continue doing other work while waiting
        System.out.println("Doing other work while waiting for driver...");

        // Wait for the result
        try {
            CompletableFuture<Driver> future = rideRequest.getResultFuture();

            // Attach callback for when complete
            future.thenAccept(driver -> {
                if (driver != null) {
                    System.out.println("ASYNC CALLBACK: Driver " + driver.getName() + " assigned!");
                } else {
                    System.out.println("ASYNC CALLBACK: No driver was assigned.");
                }
            });

            // Block here just to show the result in this demo
            Driver driver = future.get(60, TimeUnit.SECONDS);
            long totalDuration = System.currentTimeMillis() - startTime;

            if (driver != null) {
                System.out.println("SUCCESS: Ride assigned to " + driver.getName()
                        + " (total time: " + totalDuration + "ms)");
                rideService.completeRide(rideRequest.getRide().getId());
            } else {
                System.out.println("FAILED: No driver available (total time: " + totalDuration + "ms)");
            }
        } catch (Exception e) {
            System.out.println("Error waiting for async ride: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Demonstrates concurrent ride requests from multiple riders
     */
    public void demonstrateConcurrentRides() {
        System.out.println("=".repeat(60));
        System.out.println("DEMO: Concurrent Ride Requests");
        System.out.println("=".repeat(60));

        int numConcurrentRiders = 8;
        ExecutorService riderPool = Executors.newFixedThreadPool(numConcurrentRiders);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        System.out.println("Submitting " + numConcurrentRiders + " concurrent ride requests...\n");
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= numConcurrentRiders; i++) {
            final int riderId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Rider rider = new Rider("concurrent_rider" + riderId, "Rider " + riderId, "555-300" + riderId);
                Location pickup = new Location(riderId * 1.5, riderId * 1.5);
                Location dropoff = new Location(riderId * 3.0, riderId * 3.0);

                System.out.println("[Rider " + riderId + "] Requesting ride...");

                RideRequest request = rideService.requestRideAsync(rider, pickup, dropoff);

                try {
                    Driver driver = request.getResultFuture().get(90, TimeUnit.SECONDS);
                    if (driver != null) {
                        System.out.println("[Rider " + riderId + "] SUCCESS - Got driver: " + driver.getName());
                        // Simulate ride duration
                        Thread.sleep(500);
                        rideService.completeRide(request.getRide().getId());
                        System.out.println("[Rider " + riderId + "] Ride completed!");
                    } else {
                        System.out.println("[Rider " + riderId + "] FAILED - No driver available");
                    }
                } catch (Exception e) {
                    System.out.println("[Rider " + riderId + "] ERROR: " + e.getMessage());
                }
            }, riderPool);

            futures.add(future);
        }

        // Wait for all concurrent rides to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long totalDuration = System.currentTimeMillis() - startTime;
        System.out.println("\nAll " + numConcurrentRiders + " concurrent requests processed in "
                + totalDuration + "ms");

        riderPool.shutdown();
        System.out.println();
    }

    /**
     * Demonstrates high contention scenario with more riders than drivers
     */
    public void demonstrateHighContention() {
        System.out.println("=".repeat(60));
        System.out.println("DEMO: High Contention (More Riders Than Drivers)");
        System.out.println("=".repeat(60));

        // Reset all drivers to available
        for (Driver driver : driverRepository.getAllDrivers()) {
            driver.setStatus(DriverStatus.AVAILABLE);
        }
        System.out.println("Reset all drivers to AVAILABLE status.");

        int numRiders = 15; // More than NUM_DRIVERS
        List<RideRequest> requests = new ArrayList<>();

        System.out.println("Submitting " + numRiders + " ride requests (more than " + NUM_DRIVERS + " drivers)...\n");

        // Submit all requests quickly
        for (int i = 1; i <= numRiders; i++) {
            Rider rider = new Rider("contention_rider" + i, "Contention Rider " + i, "555-400" + i);
            Location pickup = new Location(i % 5 + 1, i % 5 + 1);
            Location dropoff = new Location(i + 10, i + 10);

            RideRequest request = rideService.requestRideAsync(rider, pickup, dropoff);
            requests.add(request);
        }

        System.out.println("All requests submitted. Waiting for results...\n");

        // Collect results
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            RideRequest request = requests.get(i);
            try {
                Driver driver = request.getResultFuture().get(120, TimeUnit.SECONDS);
                if (driver != null) {
                    successCount++;
                    System.out.println("Request " + (i + 1) + ": SUCCESS - Driver " + driver.getName());
                } else {
                    failCount++;
                    System.out.println("Request " + (i + 1) + ": FAILED - No driver");
                }
            } catch (Exception e) {
                failCount++;
                System.out.println("Request " + (i + 1) + ": ERROR - " + e.getMessage());
            }
        }

        System.out.println("\n--- High Contention Results ---");
        System.out.println("Success: " + successCount + "/" + numRiders);
        System.out.println("Failed:  " + failCount + "/" + numRiders);
        System.out.println();
    }

    public void shutdown() {
        System.out.println("=".repeat(60));
        System.out.println("Shutting down Uber Application...");
        System.out.println("=".repeat(60));

        // Stop all workers
        for (RideMatchingWorker worker : workers) {
            worker.stop();
        }

        // Shutdown worker pool
        workerPool.shutdown();
        try {
            if (!workerPool.awaitTermination(5, TimeUnit.SECONDS)) {
                workerPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // Shutdown notification service
        driverNotificationService.shutdown();

        System.out.println("Uber Application shut down complete.");
    }

    public static void main(String[] args) {
        UberApplication app = new UberApplication();

        try {
            app.initialize();

            // Wait a moment for workers to start
            Thread.sleep(500);

            // Run demonstrations
            app.demonstrateSyncRide();

            Thread.sleep(1000);

            app.demonstrateAsyncRide();

            Thread.sleep(1000);

            app.demonstrateConcurrentRides();

            Thread.sleep(1000);

            app.demonstrateHighContention();

        } catch (InterruptedException e) {
            System.out.println("Application interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            app.shutdown();
        }
    }
}
