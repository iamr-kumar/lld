package patterns.strategy;

public class Payment {
  private PaymentStrategy paymentStrategy;

  public Payment(PaymentStrategy paymentStrategy) {
    this.paymentStrategy = paymentStrategy;
  }

  public void pay(int amount) {
    paymentStrategy.pay(amount);
  }

  public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
    this.paymentStrategy = paymentStrategy;
  }

}
