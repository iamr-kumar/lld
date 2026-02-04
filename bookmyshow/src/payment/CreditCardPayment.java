package bookmyshow.src.payment;

import bookmyshow.src.interfaces.IPayementStrategy;

public class CreditCardPayment implements IPayementStrategy {
    final private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean makePayment(double amount) {
        // Logic to process credit card payment
        System.out.println("Processing credit card payment of " + amount + " using card number: " + cardNumber);
        return true; // Assume payment is successful for simplicity
    }

}
