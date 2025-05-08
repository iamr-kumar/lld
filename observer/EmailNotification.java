package observer;

public class EmailNotification implements IStockObserver {
    private String email;
    private IStockObservable observable;

    public EmailNotification(String email, IStockObservable observable) {
        this.observable = observable;
        this.email = email;
    }

    @Override
    public void update() {
      String productName = observable.getProductName();
      int stockCount = observable.getStockCount();
      System.out.println("Sending email to " + email + ": " + productName + " is now in stock with count: " + stockCount);
    }
  
}
