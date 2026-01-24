package abstractfactory.transport;

public class Plane implements IVehicle {

  @Override
  public void drive() {
    System.out.println("Flying in a plane...");
  }

  
}
