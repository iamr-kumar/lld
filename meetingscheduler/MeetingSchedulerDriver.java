package meetingscheduler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import meetingscheduler.src.enums.RoomType;
import meetingscheduler.src.models.Meeting;
import meetingscheduler.src.models.Room;
import meetingscheduler.src.models.TimeWindow;
import meetingscheduler.src.models.User;
import meetingscheduler.src.repository.MeetingRepository;
import meetingscheduler.src.repository.RoomRepository;
import meetingscheduler.src.services.IMeetingService;
import meetingscheduler.src.services.IRoomService;
import meetingscheduler.src.services.MeetingService;
import meetingscheduler.src.services.RoomService;

/**
 * MeetingScheduler Test Suite
 * Tests for create meetings, get meetings, cancel meetings, and concurrency.
 */
public class MeetingSchedulerDriver {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          MEETING SCHEDULER SYSTEM - TEST SUITE               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        testBasicMeetingScheduling();
        testGetMeetingsForUser();
        testCancelMeeting();
        testRoomConflict();
        testConcurrentMeetingScheduling();
        testConcurrentScheduleAndCancel();

        printTestSummary();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 1: Basic Meeting Scheduling
    // ═══════════════════════════════════════════════════════════════════
    private static void testBasicMeetingScheduling() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 1: Basic Meeting Scheduling");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        IMeetingService meetingService = createMeetingService(3);

        User host = new User("Alice", "alice@example.com");
        List<User> attendees = List.of(
                new User("Bob", "bob@example.com"),
                new User("Charlie", "charlie@example.com"));

        TimeWindow window = new TimeWindow(
                LocalDateTime.of(2026, 2, 5, 10, 0),
                LocalDateTime.of(2026, 2, 5, 11, 0));

        Optional<Meeting> result = meetingService.scheduleMeeting(
                host, "Sprint Planning", window, attendees, RoomType.CONFERENCE_ROOM);

        assertTest("Meeting should be scheduled successfully", result.isPresent());
        assertTest("Meeting title should match",
                result.isPresent() && result.get().getTitle().equals("Sprint Planning"));
        assertTest("Meeting should have a room assigned",
                result.isPresent() && result.get().getRoom() != null);

        if (result.isPresent()) {
            System.out.println("  ✓ Meeting: " + result.get().getTitle() +
                    " → Room " + result.get().getRoom().getRoomNumber());
        }
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 2: Get Meetings for User
    // ═══════════════════════════════════════════════════════════════════
    private static void testGetMeetingsForUser() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 2: Get Meetings for User");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        IMeetingService meetingService = createMeetingService(3);

        User host = new User("Alice", "alice@example.com");
        User bob = new User("Bob", "bob@example.com");
        List<User> attendees = List.of(bob);

        // Schedule 2 meetings for Alice
        meetingService.scheduleMeeting(host, "Meeting 1",
                new TimeWindow(LocalDateTime.of(2026, 2, 5, 9, 0), LocalDateTime.of(2026, 2, 5, 10, 0)),
                attendees, RoomType.CONFERENCE_ROOM);

        meetingService.scheduleMeeting(host, "Meeting 2",
                new TimeWindow(LocalDateTime.of(2026, 2, 5, 11, 0), LocalDateTime.of(2026, 2, 5, 12, 0)),
                attendees, RoomType.CONFERENCE_ROOM);

        List<Meeting> aliceMeetings = meetingService.getAllMeetingsForUser(host);
        List<Meeting> bobMeetings = meetingService.getAllMeetingsForUser(bob);

        assertTest("Alice should have 2 meetings (as host)", aliceMeetings.size() == 2);
        assertTest("Bob should have 2 meetings (as attendee)", bobMeetings.size() == 2);

        System.out.println("  Alice's meetings: " + aliceMeetings.size());
        System.out.println("  Bob's meetings: " + bobMeetings.size());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 3: Cancel Meeting
    // ═══════════════════════════════════════════════════════════════════
    private static void testCancelMeeting() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 3: Cancel Meeting");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        IMeetingService meetingService = createMeetingService(2);

        User host = new User("Alice", "alice@example.com");
        List<User> attendees = List.of(new User("Bob", "bob@example.com"));

        TimeWindow window = new TimeWindow(
                LocalDateTime.of(2026, 2, 5, 14, 0),
                LocalDateTime.of(2026, 2, 5, 15, 0));

        Optional<Meeting> meeting = meetingService.scheduleMeeting(
                host, "To Cancel", window, attendees, RoomType.CONFERENCE_ROOM);

        assertTest("Meeting should be scheduled before cancellation", meeting.isPresent());

        if (meeting.isEmpty()) {
            System.out.println("  ⚠️ Skipping remaining cancel tests - meeting not scheduled");
            System.out.println();
            return;
        }

        boolean cancelled = meetingService.cancelMeeting(meeting.get());
        assertTest("Meeting should be cancelled successfully", cancelled);

        List<Meeting> remainingMeetings = meetingService.getAllMeetingsForUser(host);
        assertTest("User should have no meetings after cancellation", remainingMeetings.isEmpty());

        // Try to cancel again (should fail)
        boolean cancelAgain = meetingService.cancelMeeting(meeting.get());
        assertTest("Cancelling already cancelled meeting should fail", !cancelAgain);

        System.out.println("  Meeting cancelled: " + cancelled);
        System.out.println("  Remaining meetings for host: " + remainingMeetings.size());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 4: Room Conflict - Same room, overlapping time
    // ═══════════════════════════════════════════════════════════════════
    private static void testRoomConflict() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 4: Room Conflict (Single Room)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        IMeetingService meetingService = createMeetingService(1); // Only 1 room

        User host1 = new User("Alice", "alice@example.com");
        User host2 = new User("Bob", "bob@example.com");
        List<User> attendees = List.of(new User("Charlie", "charlie@example.com"));

        // First meeting: 10:00 - 11:00
        Optional<Meeting> meeting1 = meetingService.scheduleMeeting(
                host1, "Meeting 1",
                new TimeWindow(LocalDateTime.of(2026, 2, 5, 10, 0), LocalDateTime.of(2026, 2, 5, 11, 0)),
                attendees, RoomType.CONFERENCE_ROOM);

        // Second meeting: 10:30 - 11:30 (overlaps with first)
        Optional<Meeting> meeting2 = meetingService.scheduleMeeting(
                host2, "Meeting 2",
                new TimeWindow(LocalDateTime.of(2026, 2, 5, 10, 30), LocalDateTime.of(2026, 2, 5, 11, 30)),
                attendees, RoomType.CONFERENCE_ROOM);

        assertTest("First meeting should be scheduled", meeting1.isPresent());
        assertTest("Second meeting should fail (room conflict)", meeting2.isEmpty());

        System.out.println("  Meeting 1 scheduled: " + meeting1.isPresent());
        System.out.println("  Meeting 2 scheduled: " + meeting2.isPresent() + " (expected: false)");
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 5: Concurrent Meeting Scheduling (Thread Safety)
    // ═══════════════════════════════════════════════════════════════════
    private static void testConcurrentMeetingScheduling() throws InterruptedException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 5: Concurrent Meeting Scheduling (Thread Safety)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        final int NUM_ROOMS = 3;
        final int NUM_THREADS = 10;
        final IMeetingService meetingService = createMeetingService(NUM_ROOMS);

        List<Optional<Meeting>> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(NUM_THREADS);
        AtomicInteger errors = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // All threads try to book the SAME time slot
        TimeWindow sameWindow = new TimeWindow(
                LocalDateTime.of(2026, 2, 5, 15, 0),
                LocalDateTime.of(2026, 2, 5, 16, 0));

        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    User host = new User("User" + threadId, "user" + threadId + "@example.com");
                    List<User> attendees = List.of(new User("Attendee", "attendee@example.com"));

                    Optional<Meeting> result = meetingService.scheduleMeeting(
                            host, "Meeting-" + threadId, sameWindow, attendees, RoomType.CONFERENCE_ROOM);
                    results.add(result);
                } catch (Exception e) {
                    errors.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // Start all threads simultaneously
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        long successCount = results.stream().filter(Optional::isPresent).count();
        long failCount = results.stream().filter(Optional::isEmpty).count();

        assertTest("No exceptions during concurrent scheduling", errors.get() == 0);
        assertTest("All requests should be processed", results.size() == NUM_THREADS);
        assertTest("Only " + NUM_ROOMS + " meetings should succeed (limited rooms)",
                successCount == NUM_ROOMS);
        assertTest("Remaining " + (NUM_THREADS - NUM_ROOMS) + " should fail (no room available)",
                failCount == NUM_THREADS - NUM_ROOMS);

        System.out.println("  Threads: " + NUM_THREADS);
        System.out.println("  Rooms available: " + NUM_ROOMS);
        System.out.println("  Successfully scheduled: " + successCount);
        System.out.println("  Failed (no room): " + failCount);
        System.out.println("  Errors: " + errors.get());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 6: Concurrent Schedule and Cancel on Same Room
    // ═══════════════════════════════════════════════════════════════════
    private static void testConcurrentScheduleAndCancel() throws InterruptedException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 6: Concurrent Schedule and Cancel on Same Room");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        final IMeetingService meetingService = createMeetingService(1); // Single room
        AtomicInteger scheduleSuccess = new AtomicInteger(0);
        AtomicInteger cancelSuccess = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);

        // Pre-schedule a meeting
        User initialHost = new User("Initial", "initial@example.com");
        Optional<Meeting> initialMeeting = meetingService.scheduleMeeting(
                initialHost, "Initial Meeting",
                new TimeWindow(LocalDateTime.of(2026, 2, 6, 10, 0), LocalDateTime.of(2026, 2, 6, 11, 0)),
                List.of(new User("Attendee", "a@example.com")), RoomType.CONFERENCE_ROOM);

        assertTest("Initial meeting should be scheduled", initialMeeting.isPresent());

        final int NUM_OPERATIONS = 20;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(NUM_OPERATIONS);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Mix of schedule and cancel operations
        for (int i = 0; i < NUM_OPERATIONS; i++) {
            final int opId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    if (opId % 2 == 0) {
                        // Try to schedule
                        User host = new User("Host" + opId, "host" + opId + "@example.com");
                        Optional<Meeting> m = meetingService.scheduleMeeting(
                                host, "Meeting-" + opId,
                                new TimeWindow(
                                        LocalDateTime.of(2026, 2, 6, 12 + (opId % 5), 0),
                                        LocalDateTime.of(2026, 2, 6, 13 + (opId % 5), 0)),
                                List.of(new User("A", "a@example.com")), RoomType.CONFERENCE_ROOM);
                        if (m.isPresent()) {
                            scheduleSuccess.incrementAndGet();
                        }
                    } else {
                        // Try to cancel initial meeting (only one will succeed)
                        if (initialMeeting.isPresent()) {
                            boolean cancelled = meetingService.cancelMeeting(initialMeeting.get());
                            if (cancelled) {
                                cancelSuccess.incrementAndGet();
                            }
                        }
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTest("No exceptions during concurrent operations", errors.get() == 0);
        assertTest("Initial meeting should be cancelled exactly once", cancelSuccess.get() == 1);

        System.out.println("  Operations: " + NUM_OPERATIONS);
        System.out.println("  Successful schedules: " + scheduleSuccess.get());
        System.out.println("  Successful cancellations: " + cancelSuccess.get());
        System.out.println("  Errors: " + errors.get());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════

    private static IMeetingService createMeetingService(int numRooms) {
        RoomRepository roomRepository = new RoomRepository();
        MeetingRepository meetingRepository = new MeetingRepository();
        IRoomService roomService = new RoomService(roomRepository);

        // Add rooms
        for (int i = 1; i <= numRooms; i++) {
            Room room = new Room(1, i, RoomType.CONFERENCE_ROOM, 10);
            roomService.addRoom(room);
        }

        return new MeetingService(roomService, meetingRepository);
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
