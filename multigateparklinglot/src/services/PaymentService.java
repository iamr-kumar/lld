package multigateparklinglot.src.services;

import multigateparklinglot.src.enums.PaymentType;
import multigateparklinglot.src.factory.PaymentFactory;
import multigateparklinglot.src.models.ticket.Ticket;
import multigateparklinglot.src.strategy.fee.FeeCalculationStrategy;
import multigateparklinglot.src.strategy.payment.PaymentStrategy;

public class PaymentService {

    public boolean processPayment(Ticket ticket, PaymentType paymentType) {
        ticket.setExitTime();
        int duration = ticket.getDurationInMinutes();
        FeeCalculationStrategy feeStrategy = ticket.getFeeCalculationStrategy();
        double fee = feeStrategy.calculateFee(duration);
        PaymentStrategy paymentStrategy = PaymentFactory.getPaymentStrategy(paymentType);
        return paymentStrategy.pay(fee);
    }
}
