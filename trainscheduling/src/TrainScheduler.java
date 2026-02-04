package trainscheduling.src;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import trainscheduling.src.enums.Status;
import trainscheduling.src.manager.IPlatformAvailabilityManager;
import trainscheduling.src.manager.PlatformAvailabilityManager;
import trainscheduling.src.models.PlatformAssignment;
import trainscheduling.src.models.ScheduleRequest;
import trainscheduling.src.models.ScheduleResponse;
import trainscheduling.src.models.TimeWindow;
import trainscheduling.src.models.Train;
import trainscheduling.src.repository.AssignmentRepository;
import trainscheduling.src.repository.IAssignmentRepository;
import trainscheduling.src.services.IPlatformService;
import trainscheduling.src.services.ISchedulingService;
import trainscheduling.src.services.PlatformService;
import trainscheduling.src.services.SchedulingService;

/**
 * TrainScheduler Test Suite
 * 
 * This class contains a comprehensive set of tests to validate the
 * functionality
 * of the Train Scheduling System. It covers basic scheduling, waiting
 * scenarios,
 * platform queries, edge cases, concurrent scheduling, concurrent read-write
 * operations, and high-volume stress testing.
 * 
 * Each test prints detailed results to the console, including pass/fail status
 * for each assertion and a summary at the end.
 * 
 * THIS IS AI GENERATED CODE FOR TESTING PURPOSES ONLY.
 * REST OF THE CODE IS WRITTEN BY ME :)
 */
public class TrainScheduler {

        private static int testsPassed = 0;
        private static int testsFailed = 0;

        public static void main(String[] args) throws InterruptedException {
                System.out.println("╔══════════════════════════════════════════════════════════════╗");
                System.out.println("║           TRAIN SCHEDULING SYSTEM - TEST SUITE               ║");
                System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

                // Run all test suites
                testBasicScheduling();
                testWaitingScenario();
                testPlatformQuery();
                testEdgeCases();
                testConcurrentScheduling();
                testConcurrentReadWrite();
                testHighVolumeScheduling();

                // Print summary
                printTestSummary();
        }

        // ═══════════════════════════════════════════════════════════════════
        // TEST 1: Basic Scheduling - Train gets assigned to available platform
        // ═══════════════════════════════════════════════════════════════════
        private static void testBasicScheduling() {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("TEST 1: Basic Scheduling");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                ISchedulingService service = createSchedulingService(4);

                // Schedule a train when platforms are free
                Train train = new Train("T001", "Rajdhani Express");
                ScheduleRequest request = new ScheduleRequest(train,
                                new TimeWindow(LocalTime.of(10, 0), LocalTime.of(10, 30)));

                ScheduleResponse response = service.scheduleTrain(request);

                // Assertions
                assertTest("Train should be SCHEDULED (not waiting)",
                                response.getStatus() == Status.SCHEDULED);

                assertTest("Platform number should be valid (1-4)",
                                response.getPlatformAssignment().getPlatform().getPlatformNumber() >= 1 &&
                                                response.getPlatformAssignment().getPlatform()
                                                                .getPlatformNumber() <= 4);

                assertTest("Assigned time should match requested time",
                                response.getPlatformAssignment().getTimeWindow().getStartTime()
                                                .equals(LocalTime.of(10, 0)));

                System.out.println("  ✓ Train: " + train.getTrainName() +
                                " → Platform " + response.getPlatformAssignment().getPlatform().getPlatformNumber() +
                                " [" + response.getStatus() + "]");
                System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════════
        // TEST 2: Waiting Scenario - Train must wait when platforms are busy
        // ═══════════════════════════════════════════════════════════════════
        private static void testWaitingScenario() {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("TEST 2: Waiting Scenario (All Platforms Busy)");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                ISchedulingService service = createSchedulingService(2); // Only 2 platforms

                // Fill both platforms
                ScheduleResponse r1 = service.scheduleTrain(new ScheduleRequest(
                                new Train("T001", "Train 1"),
                                new TimeWindow(LocalTime.of(10, 0), LocalTime.of(10, 30))));

                ScheduleResponse r2 = service.scheduleTrain(new ScheduleRequest(
                                new Train("T002", "Train 2"),
                                new TimeWindow(LocalTime.of(10, 0), LocalTime.of(10, 30))));

                // Third train arrives when both platforms are occupied
                ScheduleResponse r3 = service.scheduleTrain(new ScheduleRequest(
                                new Train("T003", "Train 3"),
                                new TimeWindow(LocalTime.of(10, 15), LocalTime.of(10, 45))));

                // Assertions
                assertTest("Train 1 should be SCHEDULED", r1.getStatus() == Status.SCHEDULED);
                assertTest("Train 2 should be SCHEDULED", r2.getStatus() == Status.SCHEDULED);
                assertTest("Train 3 should be WAITING (all platforms busy at arrival time)",
                                r3.getStatus() == Status.WAITING);

                assertTest("Train 3 should start at 10:30 (when first platform frees)",
                                r3.getPlatformAssignment().getTimeWindow().getStartTime().equals(LocalTime.of(10, 30)));

                // Duration should be preserved (30 minutes)
                assertTest("Train 3 duration should be preserved (30 min)",
                                r3.getPlatformAssignment().getTimeWindow().getEndTime().equals(LocalTime.of(11, 0)));

                System.out.println(
                                "  Train 1: Platform " + r1.getPlatformAssignment().getPlatform().getPlatformNumber() +
                                                " [10:00-10:30] " + r1.getStatus());
                System.out.println(
                                "  Train 2: Platform " + r2.getPlatformAssignment().getPlatform().getPlatformNumber() +
                                                " [10:00-10:30] " + r2.getStatus());
                System.out.println("  Train 3: Platform " + r3.getPlatformAssignment().getPlatform().getPlatformNumber()
                                +
                                " [" + r3.getPlatformAssignment().getTimeWindow().getStartTime() + "-" +
                                r3.getPlatformAssignment().getTimeWindow().getEndTime() + "] " + r3.getStatus());
                System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════════
        // TEST 3: Platform Query - Query which train is on a platform at a time
        // ═══════════════════════════════════════════════════════════════════
        private static void testPlatformQuery() {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("TEST 3: Platform Query");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                ISchedulingService service = createSchedulingService(4);

                // Schedule a train
                Train train = new Train("T001", "Shatabdi Express");
                ScheduleResponse response = service.scheduleTrain(new ScheduleRequest(
                                train, new TimeWindow(LocalTime.of(14, 0), LocalTime.of(14, 20))));

                int platformNumber = response.getPlatformAssignment().getPlatform().getPlatformNumber();

                // Query during occupied time
                Optional<PlatformAssignment> duringOccupied = service.getAssignmentForPlatformAtTime(
                                platformNumber, LocalTime.of(14, 10));

                // Query before train arrives
                Optional<PlatformAssignment> beforeArrival = service.getAssignmentForPlatformAtTime(
                                platformNumber, LocalTime.of(13, 50));

                // Query after train departs
                Optional<PlatformAssignment> afterDeparture = service.getAssignmentForPlatformAtTime(
                                platformNumber, LocalTime.of(14, 30));

                // Query at exact start time (boundary)
                Optional<PlatformAssignment> atStart = service.getAssignmentForPlatformAtTime(
                                platformNumber, LocalTime.of(14, 0));

                // Query at exact end time (boundary)
                Optional<PlatformAssignment> atEnd = service.getAssignmentForPlatformAtTime(
                                platformNumber, LocalTime.of(14, 20));

                // Assertions
                assertTest("Query during occupied time should return the train",
                                duringOccupied.isPresent() &&
                                                duringOccupied.get().getTrain().getTrainNumber().equals("T001"));

                assertTest("Query before arrival should return empty",
                                beforeArrival.isEmpty());

                assertTest("Query after departure should return empty",
                                afterDeparture.isEmpty());

                assertTest("Query at exact start time should return the train",
                                atStart.isPresent());

                assertTest("Query at exact end time should return the train",
                                atEnd.isPresent());

                System.out.println("  Platform " + platformNumber + " at 14:10 → " +
                                (duringOccupied.isPresent() ? duringOccupied.get().getTrain().getTrainName()
                                                : "Empty"));
                System.out.println("  Platform " + platformNumber + " at 13:50 → " +
                                (beforeArrival.isPresent() ? beforeArrival.get().getTrain().getTrainName() : "Empty"));
                System.out.println("  Platform " + platformNumber + " at 14:30 → " +
                                (afterDeparture.isPresent() ? afterDeparture.get().getTrain().getTrainName()
                                                : "Empty"));
                System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════════
        // TEST 4: Edge Cases
        // ═══════════════════════════════════════════════════════════════════
        private static void testEdgeCases() {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("TEST 4: Edge Cases");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                // 4a: Multiple trains arriving at exact same time
                System.out.println("\n  4a: Multiple trains at same arrival time");
                ISchedulingService service1 = createSchedulingService(2);

                ScheduleResponse sameTime1 = service1.scheduleTrain(new ScheduleRequest(
                                new Train("T001", "Train A"),
                                new TimeWindow(LocalTime.of(9, 0), LocalTime.of(9, 15))));

                ScheduleResponse sameTime2 = service1.scheduleTrain(new ScheduleRequest(
                                new Train("T002", "Train B"),
                                new TimeWindow(LocalTime.of(9, 0), LocalTime.of(9, 15))));

                assertTest("Both trains at same time should be SCHEDULED (2 platforms available)",
                                sameTime1.getStatus() == Status.SCHEDULED && sameTime2.getStatus() == Status.SCHEDULED);

                assertTest("Both trains should be on different platforms",
                                sameTime1.getPlatformAssignment().getPlatform().getPlatformNumber() != sameTime2
                                                .getPlatformAssignment().getPlatform().getPlatformNumber());

                System.out.println("      Train A → Platform " +
                                sameTime1.getPlatformAssignment().getPlatform().getPlatformNumber());
                System.out.println("      Train B → Platform " +
                                sameTime2.getPlatformAssignment().getPlatform().getPlatformNumber());

                // 4b: Query non-existent platform
                System.out.println("\n  4b: Query non-existent platform");
                Optional<PlatformAssignment> nonExistent = service1.getAssignmentForPlatformAtTime(
                                999, LocalTime.of(9, 0));

                assertTest("Query for non-existent platform should return empty",
                                nonExistent.isEmpty());

                System.out.println(
                                "      Platform 999 query → " + (nonExistent.isEmpty() ? "Empty (correct)" : "ERROR"));

                // 4c: Back-to-back scheduling (train arrives exactly when previous departs)
                System.out.println("\n  4c: Back-to-back scheduling");
                ISchedulingService service2 = createSchedulingService(1); // Single platform

                ScheduleResponse backToBack1 = service2.scheduleTrain(new ScheduleRequest(
                                new Train("T001", "First Train"),
                                new TimeWindow(LocalTime.of(10, 0), LocalTime.of(10, 30))));

                ScheduleResponse backToBack2 = service2.scheduleTrain(new ScheduleRequest(
                                new Train("T002", "Second Train"),
                                new TimeWindow(LocalTime.of(10, 30), LocalTime.of(11, 0))));

                assertTest("Second train arriving exactly at first's departure should be SCHEDULED",
                                backToBack2.getStatus() == Status.SCHEDULED);

                System.out.println("      First Train: 10:00-10:30 [" + backToBack1.getStatus() + "]");
                System.out.println("      Second Train: 10:30-11:00 [" + backToBack2.getStatus() + "]");
                System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════════
        // TEST 5: Concurrent Scheduling (Thread Safety)
        // ═══════════════════════════════════════════════════════════════════
        private static void testConcurrentScheduling() throws InterruptedException {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("TEST 5: Concurrent Scheduling (Thread Safety)");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                final int NUM_PLATFORMS = 4;
                final int NUM_TRAINS = 20;
                final ISchedulingService service = createSchedulingService(NUM_PLATFORMS);
                final List<ScheduleResponse> responses = Collections.synchronizedList(new ArrayList<>());
                final CountDownLatch startLatch = new CountDownLatch(1);
                final CountDownLatch doneLatch = new CountDownLatch(NUM_TRAINS);
                final AtomicInteger errorCount = new AtomicInteger(0);

                ExecutorService executor = Executors.newFixedThreadPool(NUM_TRAINS);

                // Create threads that will all try to schedule at once
                for (int i = 0; i < NUM_TRAINS; i++) {
                        final int trainId = i;
                        executor.submit(() -> {
                                try {
                                        startLatch.await(); // Wait for signal to start all at once

                                        Train train = new Train("T" + String.format("%03d", trainId),
                                                        "Train " + trainId);
                                        ScheduleRequest request = new ScheduleRequest(train,
                                                        new TimeWindow(LocalTime.of(12, 0), LocalTime.of(12, 15)));

                                        ScheduleResponse response = service.scheduleTrain(request);
                                        responses.add(response);

                                } catch (Exception e) {
                                        errorCount.incrementAndGet();
                                        e.printStackTrace();
                                } finally {
                                        doneLatch.countDown();
                                }
                        });
                }

                // Start all threads simultaneously
                startLatch.countDown();

                // Wait for all threads to complete
                doneLatch.await(10, TimeUnit.SECONDS);
                executor.shutdown();

                // Assertions
                assertTest("All " + NUM_TRAINS + " trains should be scheduled without errors",
                                responses.size() == NUM_TRAINS && errorCount.get() == 0);

                assertTest("No exceptions during concurrent scheduling",
                                errorCount.get() == 0);

                // Count scheduled vs waiting
                long scheduledCount = responses.stream().filter(r -> r.getStatus() == Status.SCHEDULED).count();
                long waitingCount = responses.stream().filter(r -> r.getStatus() == Status.WAITING).count();

                assertTest("First " + NUM_PLATFORMS + " trains should be SCHEDULED",
                                scheduledCount == NUM_PLATFORMS);

                assertTest("Remaining " + (NUM_TRAINS - NUM_PLATFORMS) + " trains should be WAITING",
                                waitingCount == NUM_TRAINS - NUM_PLATFORMS);

                System.out.println("  Threads: " + NUM_TRAINS);
                System.out.println("  Platforms: " + NUM_PLATFORMS);
                System.out.println("  Total responses: " + responses.size());
                System.out.println("  SCHEDULED: " + scheduledCount);
                System.out.println("  WAITING: " + waitingCount);
                System.out.println("  Errors: " + errorCount.get());
                System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════════
        // TEST 6: Concurrent Read-Write (Readers and Writers simultaneously)
        // ═══════════════════════════════════════════════════════════════════
        private static void testConcurrentReadWrite() throws InterruptedException {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("TEST 6: Concurrent Read-Write Operations");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                final int NUM_WRITERS = 5;
                final int NUM_READERS = 10;
                final ISchedulingService service = createSchedulingService(4);
                final AtomicInteger writeErrors = new AtomicInteger(0);
                final AtomicInteger readErrors = new AtomicInteger(0);
                final AtomicInteger successfulWrites = new AtomicInteger(0);
                final AtomicInteger successfulReads = new AtomicInteger(0);
                final CountDownLatch startLatch = new CountDownLatch(1);
                final CountDownLatch doneLatch = new CountDownLatch(NUM_WRITERS + NUM_READERS);

                ExecutorService executor = Executors.newFixedThreadPool(NUM_WRITERS + NUM_READERS);

                // Pre-schedule some trains for readers to query
                for (int i = 0; i < 4; i++) {
                        service.scheduleTrain(new ScheduleRequest(
                                        new Train("PRE" + i, "Pre-scheduled " + i),
                                        new TimeWindow(LocalTime.of(8, 0), LocalTime.of(8, 30))));
                }

                // Writer threads (schedule new trains)
                for (int i = 0; i < NUM_WRITERS; i++) {
                        final int id = i;
                        executor.submit(() -> {
                                try {
                                        startLatch.await();
                                        for (int j = 0; j < 5; j++) {
                                                Train train = new Train("W" + id + "_" + j, "Writer Train");
                                                ScheduleRequest request = new ScheduleRequest(train,
                                                                new TimeWindow(LocalTime.of(15, 0),
                                                                                LocalTime.of(15, 10)));
                                                service.scheduleTrain(request);
                                                successfulWrites.incrementAndGet();
                                        }
                                } catch (Exception e) {
                                        writeErrors.incrementAndGet();
                                        e.printStackTrace();
                                } finally {
                                        doneLatch.countDown();
                                }
                        });
                }

                // Reader threads (query platforms)
                for (int i = 0; i < NUM_READERS; i++) {
                        executor.submit(() -> {
                                try {
                                        startLatch.await();
                                        for (int j = 0; j < 10; j++) {
                                                // Query different platforms and times
                                                service.getAssignmentForPlatformAtTime(
                                                                (j % 4) + 1, LocalTime.of(8, 15));
                                                successfulReads.incrementAndGet();
                                        }
                                } catch (Exception e) {
                                        readErrors.incrementAndGet();
                                        e.printStackTrace();
                                } finally {
                                        doneLatch.countDown();
                                }
                        });
                }

                // Start all threads
                startLatch.countDown();
                doneLatch.await(15, TimeUnit.SECONDS);
                executor.shutdown();

                // Assertions
                assertTest("No write errors during concurrent access",
                                writeErrors.get() == 0);

                assertTest("No read errors during concurrent access",
                                readErrors.get() == 0);

                assertTest("All writes completed successfully",
                                successfulWrites.get() == NUM_WRITERS * 5);

                assertTest("All reads completed successfully",
                                successfulReads.get() == NUM_READERS * 10);

                System.out.println("  Writer threads: " + NUM_WRITERS + " (5 writes each)");
                System.out.println("  Reader threads: " + NUM_READERS + " (10 reads each)");
                System.out.println("  Successful writes: " + successfulWrites.get());
                System.out.println("  Successful reads: " + successfulReads.get());
                System.out.println("  Write errors: " + writeErrors.get());
                System.out.println("  Read errors: " + readErrors.get());
                System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════════
        // TEST 7: High Volume Scheduling (Stress Test)
        // ═══════════════════════════════════════════════════════════════════
        private static void testHighVolumeScheduling() throws InterruptedException {
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("TEST 7: High Volume Stress Test");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                final int NUM_PLATFORMS = 10;
                final int NUM_TRAINS = 100;
                final ISchedulingService service = createSchedulingService(NUM_PLATFORMS);
                final List<ScheduleResponse> responses = Collections.synchronizedList(new ArrayList<>());
                final CountDownLatch latch = new CountDownLatch(NUM_TRAINS);
                final AtomicInteger errors = new AtomicInteger(0);

                ExecutorService executor = Executors.newFixedThreadPool(20);

                long startTime = System.currentTimeMillis();

                for (int i = 0; i < NUM_TRAINS; i++) {
                        final int trainId = i;
                        executor.submit(() -> {
                                try {
                                        Train train = new Train("HV" + trainId, "High Volume Train " + trainId);
                                        // Stagger arrival times slightly
                                        int hour = 6 + (trainId / 10);
                                        int minute = (trainId % 6) * 10;
                                        ScheduleRequest request = new ScheduleRequest(train,
                                                        new TimeWindow(
                                                                        LocalTime.of(hour, minute),
                                                                        LocalTime.of(hour, minute + 5)));
                                        ScheduleResponse response = service.scheduleTrain(request);
                                        responses.add(response);
                                } catch (Exception e) {
                                        errors.incrementAndGet();
                                        e.printStackTrace();
                                } finally {
                                        latch.countDown();
                                }
                        });
                }

                latch.await(30, TimeUnit.SECONDS);
                executor.shutdown();

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                // Assertions
                assertTest("All " + NUM_TRAINS + " trains scheduled without errors",
                                responses.size() == NUM_TRAINS && errors.get() == 0);

                assertTest("Performance: Should complete in under 5 seconds",
                                duration < 5000);

                System.out.println("  Platforms: " + NUM_PLATFORMS);
                System.out.println("  Trains scheduled: " + responses.size());
                System.out.println("  Errors: " + errors.get());
                System.out.println("  Time taken: " + duration + "ms");
                System.out.println("  Throughput: " + (NUM_TRAINS * 1000 / Math.max(duration, 1)) + " trains/second");
                System.out.println();
        }

        // ═══════════════════════════════════════════════════════════════════
        // HELPER METHODS
        // ═══════════════════════════════════════════════════════════════════

        private static ISchedulingService createSchedulingService(int numPlatforms) {
                IPlatformService platformService = new PlatformService();
                for (int i = 1; i <= numPlatforms; i++) {
                        platformService.addPlatform(i);
                }

                IAssignmentRepository assignmentRepository = new AssignmentRepository();
                IPlatformAvailabilityManager platformAvailabilityManager = new PlatformAvailabilityManager(
                                platformService);

                return new SchedulingService(
                                platformAvailabilityManager,
                                assignmentRepository,
                                new ReentrantReadWriteLock());
        }

        private static void assertTest(String description, boolean condition) {
                if (condition) {
                        System.out.println("  ✅ PASS: " + description);
                        testsPassed++;
                } else {
                        System.out.println("  ❌ FAIL: " + description);
                        testsFailed++;
                }
        }

        private static void printTestSummary() {
                System.out.println("╔══════════════════════════════════════════════════════════════╗");
                System.out.println("║                      TEST SUMMARY                            ║");
                System.out.println("╠══════════════════════════════════════════════════════════════╣");
                System.out.printf("║  Total Tests: %-46d ║%n", testsPassed + testsFailed);
                System.out.printf("║  Passed:      %-46d ║%n", testsPassed);
                System.out.printf("║  Failed:      %-46d ║%n", testsFailed);
                System.out.println("╠══════════════════════════════════════════════════════════════╣");
                if (testsFailed == 0) {
                        System.out.println("║  🎉 ALL TESTS PASSED! System is working correctly.          ║");
                } else {
                        System.out.println("║  ⚠️  SOME TESTS FAILED! Please review the failures above.   ║");
                }
                System.out.println("╚══════════════════════════════════════════════════════════════╝");
        }
}
