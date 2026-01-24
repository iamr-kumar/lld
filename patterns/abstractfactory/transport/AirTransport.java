package abstractfactory.transport;

public class AirTransport implements ITransportMode {

  @Override
  public void travel() {
    System.out.println("Travelling in air transport...");
  }

}
