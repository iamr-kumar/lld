package observer;

public class MobileNotification implements IStockObserver {
    private String phoneNumber;
    private IStockObservable observable;

    public MobileNotification(String phoneNumber, IStockObservable observable) {
        this.phoneNumber = phoneNumber;
        this.observable = observable;
    }

    @Override
    public void update() {
        String productName = observable.getProductName();
        int stockCount = observable.getStockCount();
        System.out.println("Sending SMS to " + phoneNumber + ": " + productName + " is now in stock with count: " + stockCount);
    }
  
}
