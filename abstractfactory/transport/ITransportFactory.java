package abstractfactory.transport;

public interface ITransportFactory {
  public IVehicle createVehicle(String vehicleType);
}
