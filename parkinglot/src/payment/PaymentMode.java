package parkinglot.src.payment;

public enum PaymentMode {
    CASH,
    CARD,
    UPI;

    public static PaymentMode fromString(String type) {
        switch (type.toUpperCase()) {
            case "CASH":
                return CASH;
            case "CARD":
                return CARD;
            case "UPI":
                return UPI;
            default:
                throw new IllegalArgumentException("Invalid payment type: " + type);
        }
    }
}
