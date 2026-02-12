package multigateparklinglot.src.strategy.payment;

public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.println("Processing cash payment for amount: " + amount);
        return true;
    }
}
