package multigateparklinglot.src.strategy.payment;

public class UPIPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Processing UPI payment for amount: " + amount);
        return true;
    }
}
