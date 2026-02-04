package patterns.abstractfactory;

public class MacOsFactory implements IFactory {

  @Override
  public IButton createButton() {
    return new MacOsButton();
  }

  @Override
  public ITextBox createTextBox() {
    return new MacOsTextBox();
  }

}
