package factory;

public class VehicleFactory {

  public static Vehicle createVehicle(String vehicleType) {
    if(vehicleType.equalsIgnoreCase("car")) {
      return new Car();
    } else if(vehicleType.equalsIgnoreCase("bike")) {
      return new Bike();
    } else {
      throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
    }
  }
}
