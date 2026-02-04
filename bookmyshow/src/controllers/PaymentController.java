package bookmyshow.src.controllers;

import bookmyshow.src.core.User;
import bookmyshow.src.services.PaymentService;

public class PaymentController {
    final private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void processPayment(double amount, String bookingId, User user) throws Exception {
        paymentService.processPayment(amount, bookingId, user);
    }
}
