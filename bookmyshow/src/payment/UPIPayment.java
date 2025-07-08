package bookmyshow.src.payment;

import bookmyshow.src.interaces.IPayementStrategy;

public class UPIPayment implements IPayementStrategy {
    private String upiId;

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public boolean makePayment(double amount) {
        // Simulate UPI payment processing
        System.out.println("Processing UPI payment of " + amount + " using UPI ID: " + upiId);
        // In a real application, you would integrate with a UPI payment gateway here
        return true; // Assume payment is successful
    }

}
