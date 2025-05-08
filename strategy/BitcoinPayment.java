package strategy;

public class BitcoinPayment implements PaymentStrategy {
  @Override
  public void pay(int amount) {
    System.out.println("Paid " + amount + " using Bitcoin.");
  }
}
