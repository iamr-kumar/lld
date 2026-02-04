package patterns.abstractfactory;

public class Application {
  public static void main(String[] args) {
    String osType = System.getProperty("os.name").toLowerCase();
    IFactory guiFactory = GUIFactory.createFactory(osType);

    IButton button = guiFactory.createButton();
    ITextBox textBox = guiFactory.createTextBox();

    button.render();
    textBox.renderText();
  }
}
