package multigateparklinglot.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import multigateparklinglot.src.engine.ParkingEngine;
import multigateparklinglot.src.enums.FeeType;
import multigateparklinglot.src.enums.PaymentType;
import multigateparklinglot.src.enums.VehicleType;
import multigateparklinglot.src.repository.ParkingRepository;
import multigateparklinglot.src.services.ParkingService;
import multigateparklinglot.src.services.PaymentService;
import multigateparklinglot.src.services.TicketService;

public class MultiGateParkingLot {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Multi-Gate Parking Lot - Test Suite ===\n");

        testSequentialParkAndUnpark();
        testParkingFullScenario();
        testConcurrentParkFromMultipleGates();
        testConcurrentParkAndUnpark();

        printSummary();
    }

    // ---------- helpers ----------

    private static ParkingEngine createEngine(int floors, int spotsPerFloorPerType) {
        ParkingRepository repo = new ParkingRepository();
        ParkingService parkingService = new ParkingService(repo);
        TicketService ticketService = new TicketService();
        PaymentService paymentService = new PaymentService();

        for (int f = 1; f <= floors; f++) {
            parkingService.addParkingFloor(f);
            for (int s = 0; s < spotsPerFloorPerType; s++) {
                parkingService.addNewParkingSpotToFloor(f, VehicleType.CAR);
                parkingService.addNewParkingSpotToFloor(f, VehicleType.BIKE);
            }
        }
        return new ParkingEngine(parkingService, ticketService, paymentService);
    }

    // ---------- TEST 1: Sequential park & unpark ----------

    private static void testSequentialParkAndUnpark() {
        System.out.println("--- Test 1: Sequential Park & Unpark ---");

        // 2 floors, 2 car + 2 bike spots per floor = 4 car spots, 4 bike spots
        ParkingEngine engine = createEngine(2, 2);

        // Park 3 cars sequentially (simulating requests from 3 entry gates)
        Optional<String> t1 = engine.parkVehicle("KA-01-1111", VehicleType.CAR, FeeType.HOURLY);
        Optional<String> t2 = engine.parkVehicle("KA-01-2222", VehicleType.CAR, FeeType.HOURLY);
        Optional<String> t3 = engine.parkVehicle("KA-01-3333", VehicleType.CAR, FeeType.HOURLY);

        assertTest("Car 1 parked successfully", t1.isPresent());
        assertTest("Car 2 parked successfully", t2.isPresent());
        assertTest("Car 3 parked successfully", t3.isPresent());
        assertTest("All tickets are unique", t1.get().equals(t2.get()) == false && t2.get().equals(t3.get()) == false);

        // Unpark car 2 via an exit gate
        boolean u2 = engine.unparkVehicle(t2.get(), PaymentType.UPI);
        assertTest("Car 2 unparked successfully", u2);

        // Park another car in the freed spot
        Optional<String> t4 = engine.parkVehicle("KA-01-4444", VehicleType.CAR, FeeType.HOURLY);
        assertTest("Car 4 parked in freed spot", t4.isPresent());

        // Unpark with invalid ticket
        boolean invalid = engine.unparkVehicle("non-existent-ticket", PaymentType.CASH);
        assertTest("Invalid ticket returns false", !invalid);

        System.out.println();
    }

    // ---------- TEST 2: Parking full scenario ----------

    private static void testParkingFullScenario() {
        System.out.println("--- Test 2: Parking Full Scenario ---");

        // 1 floor, 2 car spots only
        ParkingEngine engine = createEngine(1, 2);

        Optional<String> t1 = engine.parkVehicle("KA-01-1111", VehicleType.CAR, FeeType.HOURLY);
        Optional<String> t2 = engine.parkVehicle("KA-01-2222", VehicleType.CAR, FeeType.HOURLY);
        assertTest("Spot 1 filled", t1.isPresent());
        assertTest("Spot 2 filled", t2.isPresent());

        // Third car should fail — lot is full for cars
        Optional<String> t3 = engine.parkVehicle("KA-01-3333", VehicleType.CAR, FeeType.HOURLY);
        assertTest("Third car rejected (lot full)", t3.isEmpty());

        // Bikes should still work
        Optional<String> b1 = engine.parkVehicle("KA-01-B001", VehicleType.BIKE, FeeType.HOURLY);
        assertTest("Bike parks even when car spots full", b1.isPresent());

        System.out.println();
    }

    // ---------- TEST 3: Concurrent park from 3 entry gates ----------

    private static void testConcurrentParkFromMultipleGates() throws InterruptedException {
        System.out.println("--- Test 3: Concurrent Park from 3 Entry Gates ---");

        // 2 floors, 3 car spots per floor = 6 car spots total
        ParkingEngine engine = createEngine(2, 3);
        int numGates = 3;
        int carsPerGate = 2; // total 6 cars for 6 spots
        int totalCars = numGates * carsPerGate;

        List<Optional<String>> tickets = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalCars);
        AtomicInteger errors = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(numGates);

        for (int gate = 0; gate < numGates; gate++) {
            final int gateId = gate;
            for (int car = 0; car < carsPerGate; car++) {
                final int carId = car;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        String plate = "GATE" + gateId + "-CAR" + carId;
                        Optional<String> ticket = engine.parkVehicle(plate, VehicleType.CAR, FeeType.HOURLY);
                        tickets.add(ticket);
                    } catch (Exception e) {
                        errors.incrementAndGet();
                        e.printStackTrace();
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }
        }

        startLatch.countDown(); // fire all at once
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        long parked = tickets.stream().filter(Optional::isPresent).count();

        assertTest("No exceptions during concurrent parking", errors.get() == 0);
        assertTest("All " + totalCars + " cars parked (enough spots)", parked == totalCars);

        // Now try one more — should fail, lot is full
        Optional<String> overflow = engine.parkVehicle("OVERFLOW-1", VehicleType.CAR, FeeType.HOURLY);
        assertTest("Overflow car rejected after lot full", overflow.isEmpty());

        System.out.println();
    }

    // ---------- TEST 4: Concurrent park and unpark (entry + exit gates) ----------

    private static void testConcurrentParkAndUnpark() throws InterruptedException {
        System.out.println("--- Test 4: Concurrent Park & Unpark (Entry + Exit Gates) ---");

        // 1 floor, 3 car spots
        ParkingEngine engine = createEngine(1, 3);

        // First fill all 3 spots sequentially
        List<String> parkedTickets = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < 3; i++) {
            Optional<String> t = engine.parkVehicle("INIT-" + i, VehicleType.CAR, FeeType.HOURLY);
            assertTest("Initial car " + i + " parked", t.isPresent());
            parkedTickets.add(t.get());
        }

        // Now concurrently: 3 exit gates unpark + 3 entry gates park new cars
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(6);
        AtomicInteger errors = new AtomicInteger(0);
        List<Boolean> unparkResults = Collections.synchronizedList(new ArrayList<>());
        List<Optional<String>> newTickets = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(6);

        // 3 exit gate threads
        for (int i = 0; i < 3; i++) {
            final String ticketId = parkedTickets.get(i);
            executor.submit(() -> {
                try {
                    startLatch.await();
                    boolean result = engine.unparkVehicle(ticketId, PaymentType.CASH);
                    unparkResults.add(result);
                } catch (Exception e) {
                    errors.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 3 entry gate threads
        for (int i = 0; i < 3; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    Optional<String> ticket = engine.parkVehicle("NEW-" + id, VehicleType.CAR, FeeType.HOURLY);
                    newTickets.add(ticket);
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

        long successfulUnparks = unparkResults.stream().filter(r -> r).count();
        long successfulParks = newTickets.stream().filter(Optional::isPresent).count();

        assertTest("No exceptions during concurrent park+unpark", errors.get() == 0);
        assertTest("All 3 unparks succeeded", successfulUnparks == 3);
        // Some new parks may succeed (spots freed by unparks), some may not (timing)
        System.out.println("  New cars parked: " + successfulParks + "/3 (depends on timing)");

        System.out.println();
    }

    // ---------- assertion + summary ----------

    private static void assertTest(String description, boolean condition) {
        if (condition) {
            testsPassed++;
            System.out.println("  PASS: " + description);
        } else {
            testsFailed++;
            System.out.println("  FAIL: " + description);
        }
    }

    private static void printSummary() {
        System.out.println("=== Summary ===");
        System.out.println("Passed: " + testsPassed);
        System.out.println("Failed: " + testsFailed);
        System.out.println("Total : " + (testsPassed + testsFailed));
    }
}
