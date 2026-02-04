package patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class Product implements IStockObservable {
  private String name;
  private int stockCount;
  private List<IStockObserver> observers = new ArrayList<>();

  public Product(String name, int stockCount) {
    this.name = name;
    this.stockCount = stockCount;
  }

  @Override
  public void addObserver(IStockObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(IStockObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers() {
    for (IStockObserver observer : observers) {
      observer.update();
    }
  }

  @Override
  public void addToStock(int stockCount) {
    if (this.stockCount == 0) {
      this.stockCount = stockCount;
      notifyObservers();
    } else {
      this.stockCount += stockCount;
    }
  }

  @Override
  public int getStockCount() {
    return stockCount;
  }

  @Override
  public String getProductName() {
    return name;
  }
}
