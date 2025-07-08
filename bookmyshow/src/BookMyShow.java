package bookmyshow.src;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bookmyshow.src.controllers.BookingController;
import bookmyshow.src.controllers.MovieController;
import bookmyshow.src.controllers.PaymentController;
import bookmyshow.src.controllers.ShowController;
import bookmyshow.src.controllers.TheatreController;
import bookmyshow.src.core.Booking;
import bookmyshow.src.core.Seat;
import bookmyshow.src.core.User;
import bookmyshow.src.interaces.IPayementStrategy;
import bookmyshow.src.interaces.ISeatLockProvider;
import bookmyshow.src.payment.UPIPayment;
import bookmyshow.src.providers.SeatLockProvider;
import bookmyshow.src.services.BookingService;
import bookmyshow.src.services.MovieService;
import bookmyshow.src.services.PaymentService;
import bookmyshow.src.services.SeatService;
import bookmyshow.src.services.ShowService;
import bookmyshow.src.services.TheatreService;
import bookmyshow.src.types.SeatCategory;

public class BookMyShow {
    public static void main(String[] args) {
        try {
            List<String> seatIds = new ArrayList<>();
            // Initialize the application, set up controllers, services, etc.
            System.out.println("Welcome to BookMyShow!");
            // This is where you would typically set up your application context,
            // initialize services, and start the user interface.

            MovieService movieService = new MovieService();
            TheatreService theatreService = new TheatreService();
            ShowService showService = new ShowService();
            ISeatLockProvider seatLockProvider = new SeatLockProvider(600);
            BookingService bookingService = new BookingService(seatLockProvider);
            SeatService seatService = new SeatService(bookingService, seatLockProvider);
            IPayementStrategy paymentStrategy = new UPIPayment();
            PaymentService paymentService = new PaymentService(paymentStrategy, bookingService);

            MovieController movieController = new MovieController(movieService);
            TheatreController theatreController = new TheatreController(theatreService);
            ShowController showController = new ShowController(seatService, showService, theatreService, movieService);
            BookingController bookingController = new BookingController(theatreService, bookingService, showService);
            PaymentController paymentController = new PaymentController(paymentService);

            System.out.println("Creating a new theatre...");
            String theatreId = theatreController.addTheatre("Cineplex");
            System.out.println("Theatre created with ID: " + theatreId);

            System.out.println("Creating a new screen in the theatre...");
            String screenId = theatreController.createScreenInTheatre(theatreId, 1);
            System.out.println("Screen created with ID: " + screenId);

            System.out.println("Creating seats in the screen...");
            for (int i = 1; i <= 10; i++) {
                SeatCategory category;
                if (i <= 5) {
                    category = SeatCategory.SILVER;
                } else if (i <= 8) {
                    category = SeatCategory.GOLD;
                } else {
                    category = SeatCategory.PLATINUM;
                }
                for (int seatNum = 1; seatNum <= 10; seatNum++) {
                    String seatId = theatreController.createSeatInScreen(screenId, "Row" + i, "Seat" + seatNum,
                            category);
                    seatIds.add(seatId);
                    System.out.println("Seat created with ID: " + seatId);
                }
            }

            System.out.println("Creating a new movie...");
            String movieId = movieController.addMovie("Inception", 180);
            System.out.println("Movie created with ID: " + movieId);

            System.out.println("Creating a new show for the movie...");
            String showId = showController.createShow(movieId, screenId, new Date(), 180);
            System.out.println("Show created with ID: " + showId);

            System.out.println("Creating a new user...");
            User user = new User("John Doe", "john@example.com");
            System.out.println("User created with ID: " + user.getId());

            System.out.println("Booking seats for the show...");
            List<String> seatIdsToBook = seatIds.subList(0, 5); // Booking first 5 seats
            String bookingId = bookingController.createBooking(showId, user, seatIdsToBook);
            System.out.println("Booking created with ID: " + bookingId);

            System.out.println("Processing payment for the booking...");
            paymentController.processPayment(500.0, bookingId, user);
            System.out.println("Payment processed successfully!");

            Booking booking = bookingService.getBookingById(bookingId);
            System.out.println("Booking Details:");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("User: " + booking.getUser().getName());
            System.out.println("Status: " + booking.getStatus());

            // CONCURRENT BOOKING EXAMPLE
            System.out.println("Simulating concurrent booking...");
            List<String> concurrentSeatsOne = new ArrayList<>();
            for (int i = 15; i < 20; i++) {
                concurrentSeatsOne.add(seatIds.get(i));
            }
            List<String> concurrentSeatsTwo = new ArrayList<>();
            for (int i = 18; i < 22; i++) {
                concurrentSeatsTwo.add(seatIds.get(i));
            }

            Thread t1 = new Thread(() -> {
                try {
                    User user1 = new User("Alice", "alice@gmail.com");
                    String concurrentBookingIdOne = bookingController.createBooking(showId, user1, concurrentSeatsOne);
                    System.out.println("Concurrent Booking One created with ID: " + concurrentBookingIdOne);
                } catch (Exception e) {
                    System.err.println("Error in Concurrent Booking One: " + e.getMessage());
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    User user2 = new User("Bob", "bob@gmail.com");
                    String concurrentBookingIdTwo = bookingController.createBooking(showId, user2, concurrentSeatsTwo);
                    System.out.println("Concurrent Booking Two created with ID: " + concurrentBookingIdTwo);
                } catch (Exception e) {
                    System.err.println("Error in Concurrent Booking Two: " + e.getMessage());
                }
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();
            System.out.println("Concurrent booking simulation completed.");

            System.out.println("Available seats for the show:");
            List<Seat> availableSeats = showController.getAvailableSeatsForShow(showId);
            availableSeats.forEach(seat -> System.out.println("Available Seat: " + seat.getId()));

        } catch (Exception e) {
            System.err.println("An error occurred while initializing the application: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
