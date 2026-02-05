package uber.src.services;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import uber.src.enums.DriverResponse;
import uber.src.models.Driver;
import uber.src.models.Ride;

public class DriverNotificationService implements IDriverNotificationService {
    private final ExecutorService notificationExecutors;
    private final Random random;

    private static final int MAX_RESPONSE_TIME_MS = 10000;
    private static final int MIN_RESPONSE_TIME_MS = 1000;

    public DriverNotificationService() {
        this.notificationExecutors = Executors.newFixedThreadPool(10);
        this.random = new Random();
    }

    @Override
    public DriverResponse sendRequestToDriverAndWaitForResponse(Driver driver, Ride request) {
        System.out.println("Notifying driver " + driver.getName() + " about ride request " + request.getId());

        Callable<DriverResponse> waitForResponseTask = () -> {
            int responseTime = random.nextInt(MAX_RESPONSE_TIME_MS - MIN_RESPONSE_TIME_MS) + MIN_RESPONSE_TIME_MS;
            Thread.sleep(responseTime);
            // Thread.sleep();
            boolean accepted = random.nextDouble() < 0.3; // 30% chance of acceptance
            System.out.println("Driver " + driver.getName() + " responded with " + (accepted ? "ACCEPT" : "REJECT")
                    + " after " + responseTime + " ms");
            return accepted ? DriverResponse.ACCEPT : DriverResponse.REJECT;
        };

        // Even though we are using a thread pool,
        // We have a blocking step later with get() which defeats the purpose of using a
        // thread pool.
        // But this helps to isolate the notification logic from the main thread.
        // And have easier handling of timeouts and cancellations.
        Future<DriverResponse> futureResponse = notificationExecutors.submit(waitForResponseTask);
        try {
            // Wait for response with a timeout slightly longer than the max response time
            // to account for any delays
            return futureResponse.get(MAX_RESPONSE_TIME_MS + 1000, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            System.out.println(
                    "Driver " + driver.getName() + " did not respond in time for ride request " + request.getId());
            futureResponse.cancel(true); // Cancel the task if it times out
            return DriverResponse.REJECT; // Treat timeout as rejection
        } catch (Exception e) {
            e.printStackTrace();
            return DriverResponse.REJECT; // Treat any exception as rejection
        }

    }

    @Override
    public void shutdown() {
        notificationExecutors.shutdown();
        try {
            if (!notificationExecutors.awaitTermination(5, TimeUnit.SECONDS)) {
                notificationExecutors.shutdownNow();
            }
        } catch (InterruptedException e) {
            notificationExecutors.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
