package abstractfactory;

public interface IFactory {
  public IButton createButton();
  public ITextBox createTextBox();
}
