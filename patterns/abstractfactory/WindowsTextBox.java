package abstractfactory;

public class WindowsTextBox implements ITextBox {

  @Override
  public void renderText() {
    System.out.println("Rendering text box in Windows style");
  }  
}
