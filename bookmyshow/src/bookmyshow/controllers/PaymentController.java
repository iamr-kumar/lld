package bookmyshow.controllers;

import bookmyshow.core.User;
import bookmyshow.services.PaymentService;

public class PaymentController {
    final private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void processPayment(double amount, String bookingId, User user) throws Exception {
        paymentService.processPayment(amount, bookingId, user);
    }
}
