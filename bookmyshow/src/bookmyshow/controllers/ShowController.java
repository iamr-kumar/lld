package bookmyshow.controllers;

import java.util.Date;
import java.util.List;

import bookmyshow.core.Movie;
import bookmyshow.core.Screen;
import bookmyshow.core.Seat;
import bookmyshow.core.Show;
import bookmyshow.core.Theatre;
import bookmyshow.services.MovieService;
import bookmyshow.services.SeatService;
import bookmyshow.services.ShowService;
import bookmyshow.services.TheatreService;

public class ShowController {
    private final SeatService seatService;
    private final ShowService showService;
    private final TheatreService theatreService;
    private final MovieService movieService;

    public ShowController(SeatService seatService, ShowService showService, TheatreService theatreService,
            MovieService movieService) {
        this.seatService = seatService;
        this.showService = showService;
        this.theatreService = theatreService;
        this.movieService = movieService;
    }

    public String createShow(String movieId, String screenId, Date startTime, Integer durationInSeconds)
            throws Exception {
        Screen screen = theatreService.getScreenById(screenId);
        Movie movie = movieService.getMovieById(movieId);
        Theatre theatre = screen.getTheatre();
        Show show = showService.createShow(movie, screen, theatre, startTime);
        return show.getId();
    }

    public List<Seat> getAvailableSeatsForShow(String showId) throws Exception {
        Show show = showService.getShowById(showId);
        if (show == null) {
            throw new IllegalArgumentException("Show not found");
        }
        return seatService.getAvailableSeats(show);
    }
}
