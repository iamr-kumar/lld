package abstractfactory.transport;

public class RoadTransport implements ITransportMode {

  @Override
  public void travel() {
    System.out.println("Travelling in road transport...");
  }
  
}
