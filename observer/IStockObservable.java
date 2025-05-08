package observer;

public interface IStockObservable {
  public void addObserver(IStockObserver observer);

  public void removeObserver(IStockObserver observer);

  public void notifyObservers();

  public void addToStock(int stockCount);

  public int getStockCount();

  public String getProductName();
}
