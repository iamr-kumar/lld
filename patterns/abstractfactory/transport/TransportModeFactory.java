package patterns.abstractfactory.transport;

public class TransportModeFactory {
  public static ITransportFactory creatTransportMode(String mode) {
    if (mode.equalsIgnoreCase("road")) {
      return new RoadTransportFactory();
    } else if (mode.equalsIgnoreCase("air")) {
      return new AirTransportFactory();
    } else if (mode.equalsIgnoreCase("water")) {
      return new WaterTransportFactory();
    } else {
      throw new IllegalArgumentException("Unknown transport mode: " + mode);
    }
  }
}
