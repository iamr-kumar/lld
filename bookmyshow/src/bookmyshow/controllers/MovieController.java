package bookmyshow.controllers;

import bookmyshow.core.Movie;
import bookmyshow.services.MovieService;

public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    public String addMovie(String name, Integer durationInMinutes) {
        Movie movie = movieService.addMovie(name, durationInMinutes);
        return movie.getId();
    }
}
