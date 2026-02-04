package bookmyshow.src.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bookmyshow.src.core.Movie;
import bookmyshow.src.core.Screen;
import bookmyshow.src.core.Show;
import bookmyshow.src.core.Theatre;

public class ShowService {
    private final Map<String, Show> shows;

    public ShowService() {
        this.shows = new HashMap<>();
    }

    public Show createShow(Movie movie, Screen screen, Theatre theatre, Date startTime) {
        Show show = new Show(movie, screen, theatre, startTime);
        shows.put(show.getId(), show);
        return show;
    }

    public Show getShowById(String id) {
        return shows.get(id);
    }

    public List<Show> getShowsByTheatreId(String theatreId) {
        final List<Show> response = new ArrayList<>();
        for (Show show : shows.values()) {
            if (show.getTheatre().getId().equals(theatreId)) {
                response.add(show);
            }
        }
        return response;
    }
}