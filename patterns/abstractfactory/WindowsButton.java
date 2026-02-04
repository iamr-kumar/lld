package patterns.abstractfactory;

public class WindowsButton implements IButton {

  @Override
  public void render() {
    System.out.println("Rendering a Windows button.");
  }

}
