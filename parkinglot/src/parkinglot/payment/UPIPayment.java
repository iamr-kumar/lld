package parkinglot.payment;

public class UPIPayment implements PaymentStrategy {
    private String upiId;

    public UPIPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public void processPayment(double amount) {
        // Simulate UPI payment processing
        System.out.println("Processing UPI payment of " + amount + " using UPI ID: " + upiId);
        // Here you would integrate with a real UPI payment gateway
    }

}
