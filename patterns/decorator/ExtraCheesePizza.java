package decorator;

public class ExtraCheesePizza extends BasePizza {

  private BasePizza basePizza;

  public ExtraCheesePizza(BasePizza basePizza) {
    this.basePizza = basePizza;
  }

  @Override
  public int getCost() {
    return basePizza.getCost() + 50; // Adding cost of extra cheese
  }
  
}
