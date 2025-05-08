package strategy;

public class Main {
  public static void main(String[] args) {
    Payment payment = new Payment(new CreditCardPayment());
    payment.pay(1000);

    payment.setPaymentStrategy(new UpiPayment());
    payment.pay(2000);

    payment.setPaymentStrategy(new BitcoinPayment());
    payment.pay(3000);
  }

}
