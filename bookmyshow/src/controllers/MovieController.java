package bookmyshow.src.controllers;

import bookmyshow.src.core.Movie;
import bookmyshow.src.services.MovieService;

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
