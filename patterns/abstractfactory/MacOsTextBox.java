package abstractfactory;

public class MacOsTextBox implements ITextBox {

  @Override
  public void renderText() {
    System.out.println("Rendering MacOS TextBox with text: ");
  }

}
