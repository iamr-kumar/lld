package multigateparklinglot.src.factory;

import multigateparklinglot.src.enums.PaymentType;
import multigateparklinglot.src.strategy.payment.CashPaymentStrategy;
import multigateparklinglot.src.strategy.payment.PaymentStrategy;
import multigateparklinglot.src.strategy.payment.UPIPaymentStrategy;

public class PaymentFactory {
    public static PaymentStrategy getPaymentStrategy(PaymentType type) {
        switch (type) {
            case CASH:
                return new CashPaymentStrategy();
            case UPI:
                return new UPIPaymentStrategy();
            default:
                return new CashPaymentStrategy();
        }
    }
}
