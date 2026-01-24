package abstractfactory;

public class MacOsButton implements IButton {
  @Override
  public void render() {
    System.out.println("Rendering a Mac OS button");
  }
}
