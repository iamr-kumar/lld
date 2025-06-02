package parkinglot.src.payment;

public class CashPayment implements PaymentStrategy {
    public CashPayment() {
    }

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing cash payment of: $" + amount);
        // Logic to handle cash payment
        // For example, update the system, print receipt, etc.
    }

}
