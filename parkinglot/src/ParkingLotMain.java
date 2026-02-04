package parkinglot.src;

import java.util.HashMap;
import java.util.Map;

import parkinglot.src.fee.BasicHourlyFeeStrategy;
import parkinglot.src.fee.ParkingFeeStrategy;
import parkinglot.src.fee.PremiumHourlyFeeStrategy;
import parkinglot.src.payment.PaymentMode;
import parkinglot.src.vehicle.Vehicle;
import parkinglot.src.vehicle.VehicleFactory;
import parkinglot.src.vehicle.VehicleType;

public class ParkingLotMain {
    public static void main(String[] args) {
        // Initialize the parking lot
        ParkingLotBuilder builder = new ParkingLotBuilder().createFloor(0, 2, 10, null).createFloor(1, 10, 2, null)
                .createFloor(2, 10, 0, null);
        ParkingLot parkingLot = builder.build();

        ParkingFeeStrategy basicFeeStrategy = new BasicHourlyFeeStrategy();
        ParkingFeeStrategy premiumFeeStrategy = new PremiumHourlyFeeStrategy(); // Replace with actual premium strategy
                                                                                // if available
        Vehicle car1 = VehicleFactory.createVehicle("KA-01-HH-1234", VehicleType.CAR, basicFeeStrategy);
        Vehicle car2 = VehicleFactory.createVehicle("KA-01-HH-5678", VehicleType.CAR, basicFeeStrategy);
        Vehicle bike1 = VehicleFactory.createVehicle("KA-01-HH-9101", VehicleType.BIKE, premiumFeeStrategy);
        try {
            Ticket ticketOne = parkingLot.parkVehicle(car1);
            Ticket ticketTwo = parkingLot.parkVehicle(car2);
            Ticket ticketThree = parkingLot.parkVehicle(bike1);

            System.out.println("Ticket One: " + ticketOne.getEntryTimestamp());
            System.out.println("Ticket Two: " + ticketTwo.getEntryTimestamp());
            System.out.println("Ticket Three: " + ticketThree.getEntryTimestamp());

            // Simulate some time passing
            Thread.sleep(5000); // Simulate 5 seconds of parking
            Map<String, String> cardPaymentDetails = new HashMap<>();
            cardPaymentDetails.put("cardNumber", "1234-5678-9876-5432");

            Map<String, String> upiPaymentDetails = new HashMap<>();
            upiPaymentDetails.put("upiId", "user@upi");

            parkingLot.unparkVehicle(ticketOne.getTicketId(), PaymentMode.CARD, cardPaymentDetails);
            parkingLot.unparkVehicle(ticketTwo.getTicketId(), PaymentMode.UPI, upiPaymentDetails);
            parkingLot.unparkVehicle(ticketThree.getTicketId(), PaymentMode.CASH, null);

        } catch (Exception e) {
            System.out.println("Error creating vehicles: " + e.getMessage());
        }

    }
}
