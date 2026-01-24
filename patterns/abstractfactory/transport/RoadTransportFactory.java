package abstractfactory.transport;

public class RoadTransportFactory implements ITransportFactory {

  public IVehicle createVehicle(String vehicleType) {
    if(vehicleType.equalsIgnoreCase("car")) {
      return new Car();
    } else {
      throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
    }
  }

}
