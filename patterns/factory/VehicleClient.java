package patterns.factory;

public class VehicleClient {
  public static void main(String[] args) {
    String vehicleType = "bike";
    Vehicle vehicle = VehicleFactory.createVehicle(vehicleType);
    vehicle.drive();
  }
}
