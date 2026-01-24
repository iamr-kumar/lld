package parkinglot.payment;

public class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    // You can fields like card number, expiry date, etc. if needed

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
        // Initialize other fields if necessary
    }

    @Override
    public void processPayment(double amount) {
        // Simulate credit card payment processing
        System.out.println("Processing credit card payment of $" + amount + " using card number: " + cardNumber);
        // Here you would integrate with a real payment gateway
    }
}
