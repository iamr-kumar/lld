package parkinglot.src.payment;

import java.util.Map;

import parkinglot.src.Ticket;

public class Payment {
    private final double amount;
    private final Ticket ticket;
    private PaymentMode paymentMode;
    private PaymentStrategy paymentStrategy;
    private final Map<String, String> paymentConfig;

    public Payment(double amount, Ticket ticket, PaymentMode paymentMode, Map<String, String> paymentConfig) {
        this.amount = amount;
        this.ticket = ticket;
        this.paymentMode = paymentMode;
        this.paymentConfig = paymentConfig;
    }

    public boolean processPayment() {
        System.out.println("Processing payment for ticket: " + ticket);
        paymentStrategy.processPayment(this.amount);
        System.out.println("Payment of $" + amount + " processed successfully.");
        return true; // Assuming payment is always successful for simplicity
    }

    public void setPaymentStrategy() {
        switch (paymentMode) {
            case CASH:
                this.paymentStrategy = new CashPayment();
                break;
            case CARD:
                String cardNumber = paymentConfig.get("cardNumber");
                this.paymentStrategy = new CreditCardPayment(cardNumber);
                break;
            case UPI:
                String upiId = paymentConfig.get("upiId");
                this.paymentStrategy = new UPIPayment(upiId);
                break;
            default:
                throw new IllegalArgumentException("Unsupported payment mode: " + paymentMode);
        }
    }
}
