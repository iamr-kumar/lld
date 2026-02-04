package patterns.abstractfactory.transport;

public class WaterTransport implements ITransportMode {

  @Override
  public void travel() {
    System.out.println("Travelling in water transport...");
  }

}
