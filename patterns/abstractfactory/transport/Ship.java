package patterns.abstractfactory.transport;

public class Ship implements IVehicle {

  @Override
  public void drive() {
    System.out.println("Driving by ship...");
  }
}
