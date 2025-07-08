package bookmyshow.src.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bookmyshow.src.core.Movie;

public class MovieService {
    private final Map<String, Movie> movies;

    public MovieService() {
        this.movies = new ConcurrentHashMap<>();
    }

    public Movie addMovie(String name, Integer durationInMinutes) {
        Movie movie = new Movie(name, durationInMinutes);
        movies.put(movie.getId(), movie);
        return movie;
    }

    public Movie getMovieById(String id) {
        return movies.get(id);
    }

}
