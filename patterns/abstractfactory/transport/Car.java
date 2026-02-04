package patterns.abstractfactory.transport;

public class Car implements IVehicle {

  @Override
  public void drive() {
    System.out.println("Driving a car...");
  }

}
