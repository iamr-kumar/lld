package patterns.abstractfactory.transport;

public class WaterTransportFactory implements ITransportFactory {

  @Override
  public IVehicle createVehicle(String vehicleType) {

    if (vehicleType.equalsIgnoreCase("ship")) {
      return new Ship();
    } else {
      throw new IllegalArgumentException("Unknown water vehicle type: " + vehicleType);
    }

  }

}
