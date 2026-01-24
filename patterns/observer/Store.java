package observer;

public class Store {
  public static void main(String[] args) {
    IStockObservable laptop = new Product("Laptop", 0);
    IStockObservable iPhone = new Product("iPhone", 0);

    IStockObserver observer1 = new EmailNotification("xyz@gmail.com", laptop);
    IStockObserver observer2 = new EmailNotification("someone@someone.com", laptop);
    IStockObserver observer3 = new MobileNotification("987192", iPhone);

    laptop.addObserver(observer1);
    laptop.addObserver(observer2);
    iPhone.addObserver(observer3);

    laptop.addToStock(10);
    iPhone.addToStock(5);

  }
}
