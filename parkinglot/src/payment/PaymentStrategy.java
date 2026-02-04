package parkinglot.src.payment;

public interface PaymentStrategy {
    void processPayment(double amount);
}
