package patterns.decorator;

public class ExtraMushroomPizza extends BasePizza {

  private BasePizza pizza;

  public ExtraMushroomPizza(BasePizza pizza) {
    this.pizza = pizza;
  }

  @Override
  public int getCost() {
    return pizza.getCost() + 50; // Adding cost of extra mushrooms
  }

}
