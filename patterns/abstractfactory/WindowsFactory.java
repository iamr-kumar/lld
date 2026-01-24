package abstractfactory;

public class WindowsFactory implements IFactory {

    @Override
    public IButton createButton() {
      return new WindowsButton();
    }

    @Override
    public ITextBox createTextBox() {
      return new WindowsTextBox();
    }
  
}
