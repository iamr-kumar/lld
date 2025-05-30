package decorator;

public class PizzaCafe {
  public static void main(String[] args) {
    BasePizza pizza = new ChickenSausagePizza();
    System.out.println("Cost of Chicken Sausage Pizza: " + pizza.getCost());

    pizza = new FarmHousePizza();
    System.out.println("Cost of FarmHouse Pizza: " + pizza.getCost());

    BasePizza extraMushroomPizza = new ExtraMushroomPizza(pizza);
    System.out.println("Cost of FarmHouse Pizza with Extra Mushrooms: " + extraMushroomPizza.getCost());
  }
}
