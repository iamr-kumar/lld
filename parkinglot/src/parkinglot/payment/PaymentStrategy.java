package parkinglot.payment;

public interface PaymentStrategy {
    void processPayment(double amount);
}
