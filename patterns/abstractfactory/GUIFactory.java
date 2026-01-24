package abstractfactory;

public class GUIFactory {
  public static IFactory createFactory(String osType) {
    if (osType.contains("mac")) {
      return new MacOsFactory();
    } else if (osType.contains("windows")) {
      return new WindowsFactory();
    } else {
      throw new IllegalArgumentException("Unknown OS type: " + osType);
    }
  }
}
