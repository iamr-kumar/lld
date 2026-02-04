package patterns.abstractfactory.transport;

public class DriveTransport {
  public static void main(String[] args) {
    String mode = "water";
    ITransportFactory factory = TransportModeFactory.creatTransportMode(mode);
    IVehicle vehicle = factory.createVehicle("ship");
    vehicle.drive();
  }
}