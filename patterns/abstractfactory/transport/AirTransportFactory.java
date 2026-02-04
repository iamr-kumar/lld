package patterns.abstractfactory.transport;

public class AirTransportFactory implements ITransportFactory {
  public IVehicle createVehicle(String vehicleType) {
    if (vehicleType.equalsIgnoreCase("boeing")) {
      return new Plane();
    } else {
      throw new IllegalArgumentException("Unknown jet type: " + vehicleType);
    }
  }
}
