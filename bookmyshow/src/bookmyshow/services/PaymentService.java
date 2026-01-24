package bookmyshow.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bookmyshow.core.Booking;
import bookmyshow.core.User;
import bookmyshow.interfaces.IPayementStrategy;

public class PaymentService {
    private final IPayementStrategy paymentStrategy;
    private final BookingService bookingService;
    private final Map<Booking, Integer> failedBookings;
    private final int MAX_FAILED_ATTEMPTS = 3;

    public PaymentService(IPayementStrategy paymentStrategy, BookingService bookingService) {
        this.paymentStrategy = paymentStrategy;
        this.bookingService = bookingService;
        this.failedBookings = new ConcurrentHashMap<>();
    }

    public boolean processPayment(double amount, String bookingId, User user) throws Exception {
        if (paymentStrategy.makePayment(amount)) {
            Booking booking = bookingService.getBookingById(bookingId);
            bookingService.confirmBooking(booking, booking.getUser());
            return true;
        } else {
            this.processFailedPayment(bookingId, user);
            return false;
        }
    }

    private void processFailedPayment(String bookingId, User user) throws Exception {
        Booking booking = bookingService.getBookingById(bookingId);
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new Exception("User does not have permission to process this payment.");
        }
        if (!failedBookings.containsKey(booking)) {
            failedBookings.put(booking, 0);
        }
        Integer failedAttempts = failedBookings.get(booking);
        Integer updatedFailedCount = failedAttempts + 1;
        failedBookings.put(booking, updatedFailedCount);

        if (updatedFailedCount > MAX_FAILED_ATTEMPTS) {
            bookingService.expireBooking(booking, user);
            failedBookings.remove(booking);
        }
    }
}
